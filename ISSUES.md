Мои мысли по каждому пункту выделены _**курсивом**_.

Обратная связь на примерах из кода:

<span>1)</span>
```java
@Test
void shouldReturnStatusCode500() throws URISyntaxException {
    makeInvalidUserRequest();
    ResponseEntity<User> userResponseEntity = makeNewUserRequest();
    assertThat(userResponseEntity.getStatusCodeValue()).isGreaterThanOrEqualTo(500);
}

// ...

private void makeInvalidUserRequest() throws URISyntaxException {
    HttpHeaders headers = getHttpHeaders();
    String invalidEmail = "user@domainru";
    jsonRequest = String.format(jsonRequest, invalidEmail);
    newUserRequest = new RequestEntity<>(jsonRequest, headers, HttpMethod.POST, new URI("http://localhost:" + port + "/register"));
}
```

Нет понимания разницы между внутренней ошибкой сервера и ошибкой валидации.

_**Согласен, в случае ошибки отрабатывает стандартный ErrorController.
Нужно определить собственный Exception Handler и отдавать сообщения об ошибках с правильным статусом**_  
_UPD: Добавил отдельный эксепшен для ошибки валидации формы регистрации (ru.bank_x.registration_service.errors.RegistrationFormNotValidException)
и @ExceptionHandler метод для него (ru.bank_x.registration_service.errors.ExceptionsHandler#handleRegistrationFormNotValid).
Это позволяет нам отдавать пользователю сообщения об ошибках без лишних данных (например, без стейктрейсов) и с нужным статусом.  
Переработал RegistrationControllerTest. Теперь для теста не поднимается tomcat, а запросы тестируются через MockMvc._

---
<span>2)</span>
```java
User savedUser = userRepository.save(registrationForm.toUser(passwordEncoder));
registerVerificationService.verifyUser(savedUser);
```
работаем с репозитарем в контроллере, потом передаем в сервис jpa сущность, в итоге поехали уровни абстракции

---
<span>3)</span>
лишний мусор в логах

```java
@PostMapping
public User processRegistration(@RequestBody @Valid RegistrationForm registrationForm, BindingResult bindingResult) throws ValidationException {
    if (bindingResult.hasErrors()) {
        String errors = bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; ", "Registration form errors: ", ""));
        log.error(errors);
```

пользователь API ввел неверные данные при вызове системы, а мусорить с уровнем ERROR будем в логах сопровожденцам. Неверно введенные данные для API-сервиса - это нормальная ситуация, которая не должна мусорить в логах.

---
<span>4)</span>
 ```java
public class RegistrationController {
    private UserRepository userRepository;
    private RegisterVerificationService registerVerificationService;
    private PasswordEncoder passwordEncoder;
    // ...
}
```

энкодер прямо в контроллере... странная затея. Контроллер довольно много на себя взял обязательств.

---
<span>5)</span>
```java
@Autowired
public RegisterVerificationService(MessagingService<RegisterVerificationRequest, 
                                   RegisterVerificationResponse> messagingService,
                                   MessageListener<RegisterVerificationResponse, Boolean> responsesListener,
                                   RegisterVerificationRequestRepository verificationRequestRepository) {
    this.messagingService = messagingService;
    this.verificationRequestRepository = verificationRequestRepository;
    taskExecutorLoop = new TasksQueueExecutorLoop<>(requestsQueue, Executors.newFixedThreadPool(4)) {
        @Override
        protected Runnable getTask(RegisterVerificationRequest item) {
            task = () -> {
                try {
                    CompletableFuture<Message<RegisterVerificationResponse>> result = proceedRequest(item);
                    Message<RegisterVerificationResponse> response = result.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    responsesListener.handleMessage(response);
                } catch (TimeoutException | ExecutionException | InterruptedException e) {
                    requestsQueue.add(item);
                }
            };
            return task;
        }
    };
}
```
слушатель не должен сам передавать управление, а должен регистировать его в контейнере, чтобы сам контейнер его вызывал

---
<span>6)</span>
```java
MessageId sendRequestToVerify(RegisterVerificationRequest request) throws InterruptedException {
    try {
        return messagingService.send(new GenericMessage<>(request));
    } catch (TimeoutException e) {
        log.error(String.format(TIMEOUT_MSG, "request", request));
        return sendRequestToVerify(request);
    }
}
```
ушел на рекурсию - когда-нибудь возможно переполнение стэка.

_**Переполнения стэка не будет потому что:  
1)Из-за условия в RandomBehaviorUtils, что перед TimeoutException тред засыпает на 
минуту  -> рекурсивные вызовы редки   
2)CompletableFuture.get() не даст проработать рекурсии дольше установленного таймаута**_

---
<span>7)</span>
```java
/**
 * Завершает работу {@code taskExecutorLoop}, таким образом заставляя его вернуть все неотработанные заявки
 * обратно в {@code requestsQueue} для последующего сохранения в БД
 */
@Override
public void destroy() throws Exception {
    taskExecutorLoop.shutdown();
    verificationRequestRepository.saveAll(requestsQueue);
    log.info("{} verification requests from queue saved to DB", requestsQueue.size());
}
```

иногда JVM могут убить OOM-киллером. В такой ситуации все висящие в оперативке заявки, вероятно, потеряются, есть планировщик, он завязался вроде бы на флаги:

```java
@Override
public void afterPropertiesSet() throws Exception {
    restoreMessages();
    taskExecutorLoop.startWorkingLoop();
}

private void restoreMessages() {
    Iterable<User> notNotifiedUsers = userRepository.findAllByNotifiedAndVerifiedIsNotNull(false);
    notNotifiedUsers.forEach(user -> {
        RegisterVerificationRequest req = new RegisterVerificationRequest(user);
        RegisterVerificationResponse resp = new RegisterVerificationResponse(req, user.isVerified());
        try {
            messagesQueue.put(new GenericMessage<>(resp));
        } catch (InterruptedException e) {
            log.error("Thread interrupted while {} initialization", this.getClass().getSimpleName());
            Thread.currentThread().interrupt();
        }
    });
    log.info("{} unsent messages was restored from DB", messagesQueue.size());
}

```

при запуске приложения вытягивает все заявки из БД, которые не были обработаны. Но код плохо структурирован, в итоге сложно найти нужное
вся обработка ошибок свелась в обматывание в try/catch на каждый чих, что совсем не прибавляет читаемости решению.

_**Использование метода put() блокирующей очереди привело к необходимости обработки InterruptedException.   
Т.к. вызов метода в лямбде, то в сигнатуре не прописать. Для читаемости можно использовать `@SneakyThrows`**_

---
<span>8)</span>
```java
@Component
public class SimplePasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
    }
}
```

пароли в base64, когда у spring security есть все необходимое, чтобы надежно хешировать и солить пароли:

```java
package ru.bank_x.registration_service.utils;
public interface PasswordEncoder {
    String encode(String password);
}
```
_**Не хотелось тянуть spring security и заниматься переопределением `WebSecurityConfigurerAdapter`. Решил, что для демонстрационных целей base64 будет достаточно**_

---
<span>9)</span>
```java
@Data
@Entity
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class User {
    // ...
    @Embedded
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private final FIO fio;
    // ...
```

С JPA тоже все слабовато.  
@Embedded - это встраиваемый объект, атрибуты которого хранятся в той же таблице.  
@OneToOne - это же реальная связь с другой таблицей.  
Так что тут попытка использовать взаимоисключающие параграфы.  
(_**Изначально FIO было @Embedded, затем решил перенести FIO в отдельную таблицу. Момент про то, что это взаимоисключающие аннотации как-то упустил**_)   
equals и hashCode тоже неверные. (_**использовал генерацию от lombok, забыл исключить ненужные поля**_)  
lombok не используется хотя подключен (_**тут я не понял, возможно про неиспользование `@SneakyThrows`**_)

---
<span>10)</span>
```java
@Value
@Entity
@AllArgsConstructor
@Table(name = "register_verify_req")
public class RegisterVerificationRequest {
```

Entity внезапно в package с dto лежит, причем используется в dto и в базу кладется в случае ошибок.