package com.hbsoo.handler.message.router;

import com.hbsoo.handler.message.router.model.MessageTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * Created by zun.wei on 2021/8/10.
 */
public final class MessageDispatcher {


    static BlockingQueue<MessageTask> queue = new DelayQueue<>();

    static {
        try {
            // 从队列中获取任务，并执行任务
            MessageTask task = queue.take();
            //task.handler(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void dispatchMsg(MessageTask task) {
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
