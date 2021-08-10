package com.hbsoo.handler.message.router.model;

import com.hbsoo.handler.constants.ServerProtocolType;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/8/10.
 */
@Data
@Accessors(chain = true)
public final class MessageTask implements Delayed {

    private final long timeToRun;

    private ServerProtocolType protocolType;

    private ChannelHandlerContext ctx;

    private Object msg;


    public MessageTask() {
        this.timeToRun = 0L;
    }

    /**
     * 延迟秒数
     * @param timeLeft 延迟时间(秒)
     */
    public MessageTask(long timeLeft) {
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
