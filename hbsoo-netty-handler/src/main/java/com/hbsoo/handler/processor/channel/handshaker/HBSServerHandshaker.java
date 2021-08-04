package com.hbsoo.handler.processor.channel.handshaker;

import com.hbsoo.commons.NettyServerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.function.Consumer;

import static com.hbsoo.handler.constants.Constants.HANDSHAKE_KEY;

/**
 * Created by zun.wei on 2021/8/3.
 */
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
        addChannelConsumer.accept(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            super.channelRead(ctx, msg);
            return;
        }
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            int readableBytes = byteBuf.readableBytes();
            if (readableBytes >= 4) {
                short magicNum = byteBuf.getShort(0);//magicNum

                // 如果不是握手消息，判断管道中的的属性是否握手成功
                if (magicNum != NettyServerConstants.HANDSHAKE_MAGIC_NUM) {
                    final Boolean isHandshake = ctx.channel().attr(HANDSHAKE_KEY).get();
                    if (!isHandshake) {
                        ctx.channel().close();
                        removeChannelConsumer.accept(ctx.channel());
                        return;
                    }
                    byteBuf.retain();
                    super.channelRead(ctx, byteBuf);
                    return;
                }
                byteBuf.skipBytes(2);
                final short reqShort = byteBuf.readShort();
                if (NettyServerConstants.HANDSHAKE_CLIENT_REQ_1 == reqShort) {
                    final ByteBuf buffer = Unpooled.buffer(4);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_MAGIC_NUM);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_SERVER_RESP_1);
                    ctx.channel().writeAndFlush(buffer);
                    return;
                }
                if (NettyServerConstants.HANDSHAKE_CLIENT_REQ_2 == reqShort) {
                    final ByteBuf buffer = Unpooled.buffer(4);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_MAGIC_NUM);
                    buffer.writeShort(NettyServerConstants.HANDSHAKE_SERVER_RESP_2);
                    ctx.channel().writeAndFlush(buffer);
                    ctx.channel().attr(HANDSHAKE_KEY).set(true);
                    return;
                }

                ctx.channel().close();
                removeChannelConsumer.accept(ctx.channel());
                return;
            } else {
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
