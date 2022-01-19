package com.hbsoo.handler.processor.message;

import com.hbsoo.commons.NettyServerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.function.Consumer;

import static com.hbsoo.handler.constants.Constants.HANDSHAKE_KEY;

/**
 * Created by zun.wei on 2022/1/19.
 */
@Slf4j
public class ChannelControlHandler extends ChannelInboundHandlerAdapter {

    private final Consumer<Channel> addChannelConsumer;
    private final Consumer<Channel> removeChannelConsumer;

    public ChannelControlHandler(Consumer<Channel> addChannelConsumer,
                               Consumer<Channel> removeChannelConsumer) {
        super();
        this.addChannelConsumer = addChannelConsumer;
        this.removeChannelConsumer = removeChannelConsumer;
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
        addChannelConsumer.accept(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        removeChannelConsumer.accept(ctx.channel());
        super.channelInactive(ctx);
    }


}
