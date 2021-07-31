package com.hbsoo.handler.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zun.wei on 2021/7/31.
 */
public interface MessageHandler<MSG> {


    void handler(ChannelHandlerContext ctx, MSG msg);

}
