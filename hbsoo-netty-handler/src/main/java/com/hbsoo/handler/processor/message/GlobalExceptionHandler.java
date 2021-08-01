package com.hbsoo.handler.processor.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zun.wei on 2021/7/30.
 */
@Slf4j
public class GlobalExceptionHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //log.error("GlobalExceptionHandler channelRead0 --::{}", msg.getMessage());
        //msg.printStackTrace();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        //cause.printStackTrace();
        //HBSMessage<Throwable> message = new HBSMessage<>();
        //ctx.channel().writeAndFlush(message);
    }
}
