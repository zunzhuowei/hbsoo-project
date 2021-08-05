package com.hbsoo.utils.delayQueue;

import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

/**
 * Created by zun.wei on 2021/8/5.
 */
public class DelayQueueConsumer  implements Runnable{

    private BlockingQueue<Task> queue;
    private Integer numberOfElementsToProduce;

    public DelayQueueConsumer(BlockingQueue<Task> queue, Integer numberOfElementsToProduce) {
        this.queue = queue;
        this.numberOfElementsToProduce = numberOfElementsToProduce;
    }

    @Override
    public void run() {
        IntStream.range(0, numberOfElementsToProduce).forEach(i -> {

            try {
                // 从队列中获取任务，并执行任务
                Task task = queue.take();
                task.doWork();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

}
