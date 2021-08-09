package com.hbsoo.handler.processor.channel.handshaker;

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
 * Created by zun.wei on 2021/8/3.
 */
@Slf4j
public class HBSServerHandshaker extends ChannelInboundHandlerAdapter {


    private final Consumer<Channel> addChannelConsumer;
    private final Consumer<Channel> removeChannelConsumer;

    public HBSServerHandshaker(Consumer<Channel> addChannelConsumer,
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
        // 如果已经握手过了，则直接跳到下层处理
        final Boolean isHandshake = ctx.channel().attr(HANDSHAKE_KEY).get();
        if (Objects.isNull(isHandshake) || !isHandshake) {
            addChannelConsumer.accept(ctx.channel());
            return;
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            // 如果已经握手过了，则直接跳到下层处理
            final Boolean isHandshake = ctx.channel().attr(HANDSHAKE_KEY).get();
            if (Objects.nonNull(isHandshake) && isHandshake) {
                byteBuf.retain();
                super.channelRead(ctx, byteBuf);
                return;
            }
            try {
                // 不满足握手长度消息，直接关闭通道
                int readableBytes = byteBuf.readableBytes();
                if (readableBytes < 4) {
                    ctx.channel().close();
                    removeChannelConsumer.accept(ctx.channel());
                    return;
                }
                short magicNum = byteBuf.getShort(0);//magicNum
                // 如果不是握手消息，直接关闭通道
                if (magicNum != NettyServerConstants.HANDSHAKE_MAGIC_NUM) {
                    ctx.channel().close();
                    removeChannelConsumer.accept(ctx.channel());
                    return;
                }
                // 握手逻辑
                byteBuf.skipBytes(2);
                final short reqShort = byteBuf.readShort();
                final SocketAddress address = ctx.channel().remoteAddress();
                if (NettyServerConstants.HANDSHAKE_CLIENT_REQ_1 == reqShort) {
                    log.info("server channelRead res client [{}] handshake req 1 ", address);
                    final ByteBuf buffer = Unpooled.buffer(4);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_MAGIC_NUM);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_SERVER_RESP_1);
                    ctx.channel().writeAndFlush(buffer);
                    log.info("server channelRead response client [{}] handshake resp1 ", address);
                }
                if (NettyServerConstants.HANDSHAKE_CLIENT_REQ_2 == reqShort) {
                    log.info("server channelRead res client [{}] handshake req 2 ", address);
                    final ByteBuf buffer = Unpooled.buffer(4);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_MAGIC_NUM);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_SERVER_RESP_2);
                    ctx.channel().writeAndFlush(buffer);
                    ctx.channel().attr(HANDSHAKE_KEY).set(true);
                    log.info("server channelRead response client [{}] handshake resp2 ", address);
                }
            } finally {
                byteBuf.release();
                return;
            }
        }
        ctx.channel().close();
        removeChannelConsumer.accept(ctx.channel());
        return;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
