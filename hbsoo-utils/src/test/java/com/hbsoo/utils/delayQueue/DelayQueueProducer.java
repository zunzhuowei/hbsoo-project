package com.hbsoo.utils.delayQueue;

import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

/**
 * Created by zun.wei on 2021/8/5.
 */
public class DelayQueueProducer  implements Runnable{

    // 容器
    private BlockingQueue<Task> queue;
    // 生产指定的数量
    private Integer numberOfElementsToProduce;
    private Long timeLeft;

    public DelayQueueProducer(BlockingQueue<Task> queue, Integer numberOfElementsToProduce, long timeLeft) {
        this.queue = queue;
        this.numberOfElementsToProduce = numberOfElementsToProduce;
        this.timeLeft = timeLeft;
    }

    @Override
    public void run() {
        IntStream.range(0, numberOfElementsToProduce).forEach(i -> {
            try {
                // 向队列中存入任务
                queue.put(new Task(timeLeft, String.format("task_%s", i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
