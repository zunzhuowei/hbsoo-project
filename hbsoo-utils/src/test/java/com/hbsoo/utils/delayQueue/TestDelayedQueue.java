package com.hbsoo.utils.delayQueue;

import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zun.wei on 2021/8/5.
 */
public class TestDelayedQueue {

    public static void main(String[] args) {

        BlockingQueue<Task> taskBlockingQueue = new DelayQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 延迟执行的时间
        long time = RandomUtils.nextLong(1000, 5000);

        executorService.submit(new DelayQueueProducer(taskBlockingQueue, 3, time));
        executorService.submit(new DelayQueueConsumer(taskBlockingQueue, 3));

        executorService.shutdown();
        //WaitUtils.waitUntil(() -> executorService.isTerminated(), 100000l);
    }

}
