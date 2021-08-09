package com.hbsoo.handler.processor.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import static com.hbsoo.commons.NettyServerConstants.PING;
import static com.hbsoo.commons.NettyServerConstants.PONG;

/**
 * Created by zun.wei on 2021/8/9.
 */
@Slf4j
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {


    int count = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
            String eventType = null;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            log.info("client [{}] timeout ping count = {}", ctx.channel().remoteAddress(), count);
            count ++;
            if (count > 3) {
                ctx.disconnect();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            byteBuf.markReaderIndex();
            final int ping = byteBuf.readInt();
            if (ping == PING) {
                final ByteBuf buffer = Unpooled.buffer(4);
                buffer.writeInt(PONG);
                ctx.channel().writeAndFlush(buffer);
                ReferenceCountUtil.release(msg);
                count = 0;
            } else {
                byteBuf.resetReaderIndex();
                super.channelRead(ctx, byteBuf);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
