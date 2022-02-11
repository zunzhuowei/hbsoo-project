package com.hbsoo.handler.processor.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * Created by zun.wei on 2022/1/19.
 */
@Slf4j
public class ChannelControlHandler extends ChannelInboundHandlerAdapter {

    private final Consumer<Channel> addChannelConsumer;
    private final Consumer<Channel> removeChannelConsumer;
    private final Consumer<ChannelHandlerContext> channelAddConsumer;
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
        addChannelConsumer.accept(ctx.channel());
        channelAddConsumer.accept(ctx);
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        removeChannelConsumer.accept(ctx.channel());
        channelRemoveConsumer.accept(ctx);
        super.handlerRemoved(ctx);
    }
}
