package com.hbsoo.handler.message.router.model;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/8/10.
 */
public final class MessageTask implements Delayed {

    private final long timeToRun;

    public MessageTask() {
        this.timeToRun = 0L;
    }

    public MessageTask(long timeLeft) {
        this.timeToRun = timeLeft + System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = timeToRun - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long gap = this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
        return Long.valueOf(gap).intValue();
    }

}
