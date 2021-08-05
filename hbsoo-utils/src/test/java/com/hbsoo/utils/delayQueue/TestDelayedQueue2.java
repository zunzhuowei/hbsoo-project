package com.hbsoo.utils.delayQueue;

import java.util.concurrent.*;

/**
 * Created by zun.wei on 2021/8/5.
 */
public class TestDelayedQueue2 {

    public static void main(String[] args) {

        BlockingQueue<Task> taskBlockingQueue = new DelayQueue<>();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 生产者线程向 DelayQueue 队列中存入一个 10s 后执行的任务
        executorService.submit(new DelayQueueProducer(taskBlockingQueue, 1, 10_000));
        // 消费者线程要求 5s 后获取到执行结果
        Future<?> consumerFuture = executorService.submit(new DelayQueueConsumer(taskBlockingQueue, 1));

        // 停止接受新的任务，并在所有任务执行完毕后关闭
        executorService.shutdown();
        try {
            consumerFuture.get(5_000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 如果出现异常，立即停止所有任务的执行
            executorService.shutdownNow();
        }
    }

}
