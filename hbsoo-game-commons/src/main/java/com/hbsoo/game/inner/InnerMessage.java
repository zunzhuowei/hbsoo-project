package com.hbsoo.game.inner;

import lombok.Data;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/8/31.
 */
@Data
public class InnerMessage implements Delayed, Serializable {
    /**
     * 延迟时间
     */
    private final long timeToRun;

    /**
     * 消息类型
     */
    private int msgType;

    /**
     * 传输数据json对象
     */
    private String dataJson;

    /**
     * 是否为批量对象
     */
    private boolean batch = false;

    /**
     * 发送目的地的服务器id;如果目标服务器id不一样则不处理消息
     */
    private String toServerId;

    /**
     * 来自哪个服务器的消息
     */
    private String fromServerId;


    public InnerMessage() {
        this.timeToRun = 0L;
    }

    /**
     * 延迟秒数
     *
     * @param timeLeft 延迟时间(秒)
     */
    public InnerMessage(long timeLeft) {
        this.timeToRun = (timeLeft * 1000L) + System.currentTimeMillis();
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
