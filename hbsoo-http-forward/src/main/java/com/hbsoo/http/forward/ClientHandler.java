package com.hbsoo.http.forward;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by zun.wei on 2021/8/9.
 */
public final class ClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {


    private final ChannelHandlerContext context;

    public ClientHandler(ChannelHandlerContext context) {
        this.context = context;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        final FullHttpResponse duplicate = msg.duplicate();
        this.context.writeAndFlush(duplicate);
        ctx.close();
    }

}
