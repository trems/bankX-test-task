package ru.bank_x.registration_service.executors;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Абстрактный event loop, работающий в отдельном потоке и запускающий задачи {@code task} в отдельном тред пуле {@code taskExecutorsPool}.
 * @param <T> тип элементов, содержащихся в очереди событий {@code itemsQueue}
 */
@Slf4j
public abstract class TasksQueueExecutorLoop<T> {

    protected Thread taskStarterLoop;
    protected volatile boolean shouldWork;
    protected ExecutorService taskExecutorsPool;
    protected Runnable taskStarterRunnable;
    protected Runnable task;
    protected int awaitShutdownSeconds = 30;

    protected BlockingQueue<T> itemsQueue;

    public TasksQueueExecutorLoop(BlockingQueue<T> itemsQueue, ExecutorService threadPool) {
        this.taskExecutorsPool = threadPool;
        this.itemsQueue = itemsQueue;
        this.taskStarterRunnable = getTaskStarterRunnable(itemsQueue);
    }

    protected Runnable getTaskStarterRunnable(BlockingQueue<T> itemsQueue) {
        return () -> {
            while (this.shouldWork) {
                T item = null;
                try {
                    item = itemsQueue.take();
                } catch (InterruptedException e) {
                    log.error("{} working loop was interrupted", this.getClass().getSimpleName(), e);
                    Thread.currentThread().interrupt();
                }

                if (item == null) {
                    continue;
                }
                this.taskExecutorsPool.submit(getTask(item));
            }
        };
    }

    /**
     * Для оверрайда. Определяет задачу для тред пула
     */
    protected abstract Runnable getTask(T item);

    public void startWorkingLoop() {
        this.shouldWork = true;
        this.taskStarterLoop = new Thread(this.taskStarterRunnable);
        this.taskStarterLoop.start();
    }

    /**
     * Завершение работы класса.
     * Вызывающий тред блокируется до завершения работы тред пула и {@code taskStarterLoop}
     * @throws InterruptedException если вызывающий тред был прерван во время ожидания завершения работы тред пула
     */
    public void shutdown() throws InterruptedException {
        taskExecutorsPool.shutdownNow();
        shouldWork = false;
        if (taskExecutorsPool.awaitTermination(awaitShutdownSeconds, TimeUnit.SECONDS)) {
            taskStarterLoop.interrupt();
        }
        taskStarterLoop.join();
    }
}
