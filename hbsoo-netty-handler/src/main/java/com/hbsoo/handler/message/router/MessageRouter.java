package com.hbsoo.handler.message.router;

import io.netty.channel.ChannelHandlerContext;

/**
 *  消息处理器
 * Created by zun.wei on 2021/7/31.
 */
public interface MessageRouter<MSG> {

    /**
     * 处理 netty 管道消息
     * @param ctx 管道上下文
     * @param msg 消息类
     */
    void handler(ChannelHandlerContext ctx, MSG msg);

}
