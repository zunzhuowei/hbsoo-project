package com.hbsoo.utils.delayQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zun.wei on 2021/8/5.
 */
public class TestDelayedQueue3 {


    public static void main(String[] args) {

        BlockingQueue<Task> taskBlockingQueue = new DelayQueue<>();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 生产者线程向 DelayQueue 队列中存入一个 10s 后执行的任务
        executorService.submit(new DelayQueueProducer(taskBlockingQueue, 1, 0));
        executorService.submit(new DelayQueueConsumer(taskBlockingQueue, 1));

        // 停止接受新的任务，并在所有任务执行完毕后关闭
        executorService.shutdown();
        //WaitUtils.waitUntil(() -> executorService.isTerminated(), 100000l);
    }
}
