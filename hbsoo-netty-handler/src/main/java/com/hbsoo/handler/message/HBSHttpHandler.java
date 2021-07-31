package com.hbsoo.handler.message;

import com.hbsoo.handler.utils.SpringBeanFactory;
import com.hbsoo.msg.annotation.HttpHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by zun.wei on 2021/7/30.
 */
@Slf4j
public class HBSHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        log.debug("HBSStringHandler channelRead0 msg --::{}", msg);
        String uri = msg.uri();
        String[] split = uri.split("[?]");
        final List<MessageHandler> handlers = SpringBeanFactory.getBeansOfTypeWithAnnotation(MessageHandler.class, HttpHandler.class);
        for (MessageHandler handler : handlers) {
            final HttpHandler httpHandler = handler.getClass().getAnnotation(HttpHandler.class);
            final String[] values = httpHandler.value();
            for (String value : values) {
                if (value.equals(split[0])) {
                    handler.handler(ctx, msg);
                }
            }
        }
    }


}
