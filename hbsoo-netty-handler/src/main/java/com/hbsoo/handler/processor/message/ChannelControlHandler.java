package com.hbsoo.handler.processor.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by zun.wei on 2022/1/19.
 */
@Slf4j
public class ChannelControlHandler extends ChannelInboundHandlerAdapter {

    /**
     * 管道加入 channelManager 消费函数
     */
    private final Consumer<Channel> addChannelConsumer;
    /**
     * 管道从 channelManager 移除消费函数
     */
    private final Consumer<Channel> removeChannelConsumer;
    /**
     * 监听管道连接添加的消费函数
     */
    private final Consumer<ChannelHandlerContext> channelAddConsumer;
    /**
     * 监听管道移除的消费函数
     */
    private final Consumer<ChannelHandlerContext> channelRemoveConsumer;

    public ChannelControlHandler(Consumer<Channel> addChannelConsumer,
                               Consumer<Channel> removeChannelConsumer,
                                 Consumer<ChannelHandlerContext> channelAddConsumer,
                                 Consumer<ChannelHandlerContext> channelRemoveConsumer) {
        super();
        this.addChannelConsumer = addChannelConsumer;
        this.removeChannelConsumer = removeChannelConsumer;
        this.channelAddConsumer = channelAddConsumer;
        this.channelRemoveConsumer = channelRemoveConsumer;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (Objects.nonNull(channelAddConsumer)) {
            channelAddConsumer.accept(ctx);
        }
        addChannelConsumer.accept(ctx.channel());
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (Objects.nonNull(channelRemoveConsumer)) {
            channelRemoveConsumer.accept(ctx);
        }
        removeChannelConsumer.accept(ctx.channel());
        super.handlerRemoved(ctx);
    }
}
