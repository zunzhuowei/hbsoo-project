package com.hbsoo.handler.processor.channel.handshaker;

import com.hbsoo.commons.NettyServerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Consumer;

import static com.hbsoo.handler.constants.Constants.HANDSHAKE_KEY;

/**
 * Created by zun.wei on 2021/8/3.
 */
@Slf4j
public class HBSClientHandshaker extends ChannelInboundHandlerAdapter {

    private final Consumer<Channel> addChannelConsumer;
    private final Consumer<Channel> removeChannelConsumer;

    public HBSClientHandshaker(Consumer<Channel> addChannelConsumer,
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
        //super.channelActive(ctx);
        final ByteBuf buffer = Unpooled.buffer(4);
        buffer.writeShort(NettyServerConstants.HANDSHAKE_MAGIC_NUM);
        buffer.writeShort(NettyServerConstants.HANDSHAKE_CLIENT_REQ_1);
        ctx.channel().writeAndFlush(buffer);
        addChannelConsumer.accept(ctx.channel());
        log.info("client channelActive send handshake msg 1");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            final Boolean isHandshake = ctx.channel().attr(HANDSHAKE_KEY).get();
            // 如果已经握手，则直接给下层处理
            if (Objects.nonNull(isHandshake) && isHandshake) {
                byteBuf.retain();
                super.channelRead(ctx, byteBuf);
                return;
            }
            try {
                // 如果消息长度小于握手消息长度，直接关闭连接
                int readableBytes = byteBuf.readableBytes();
                if (readableBytes < 4) {
                    ctx.channel().close();
                    removeChannelConsumer.accept(ctx.channel());
                    return;
                }
                // 如果不是握手消息，直接关闭通道
                short magicNum = byteBuf.getShort(0);//magicNum
                if (magicNum != NettyServerConstants.HANDSHAKE_MAGIC_NUM) {
                    ctx.channel().close();
                    removeChannelConsumer.accept(ctx.channel());
                    return;
                }
                // 握手逻辑
                byteBuf.skipBytes(2);
                final short reqShort = byteBuf.readShort();
                if (NettyServerConstants.HANDSHAKE_SERVER_RESP_1 == reqShort) {
                    log.info("client channelRead res server handshake resp1 ");
                    final ByteBuf buffer = Unpooled.buffer(4);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_MAGIC_NUM);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_CLIENT_REQ_2);
                    ctx.channel().writeAndFlush(buffer);
                    log.info("client channelRead send handshake msg 2");
                }
                if (NettyServerConstants.HANDSHAKE_SERVER_RESP_2 == reqShort) {
                    ctx.channel().attr(HANDSHAKE_KEY).set(true);
                    log.info("client channelRead res server handshake resp2 ");
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
