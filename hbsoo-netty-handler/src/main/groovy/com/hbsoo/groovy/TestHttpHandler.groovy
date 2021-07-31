package com.hbsoo.groovy

import com.hbsoo.handler.message.MessageHandler
import com.hbsoo.msg.annotation.HttpHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest

/**
 * Created by zun.wei on 2021/7/31.
 *
 */
@HttpHandler(value = ["", "/"])
class TestHttpHandler implements MessageHandler<FullHttpRequest> {


    @Override
    void handler(ChannelHandlerContext ctx, FullHttpRequest request) {
        println request
        ctx.channel().closeFuture()
    }

}
