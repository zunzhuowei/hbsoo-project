package com.hbsoo.client.heartbeat;

import com.hbsoo.client.HbsooClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {


    private final HbsooClient hbsooClient;

    public HeartbeatHandler(HbsooClient hbsooClient) {
        this.hbsooClient = hbsooClient;
    }

    int count = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
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
            if (count > 5) {
                log.warn("disconnect server and reConnect !");
                ctx.disconnect();
                hbsooClient.reConnect();
                return;
            }
            log.info("send ping to server {},fail count = {}", ctx.channel().remoteAddress(), count);
            count++;
            final ByteBuf buffer = Unpooled.buffer(4);
            buffer.writeInt(PING);
            ctx.channel().writeAndFlush(buffer);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            byteBuf.markReaderIndex();
            final int pong = byteBuf.readInt();
            if (pong == PONG) {
                count = 0;
                ReferenceCountUtil.release(msg);
            } else {
                byteBuf.resetReaderIndex();
                super.channelRead(ctx, byteBuf);
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
