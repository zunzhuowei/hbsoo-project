package com.hbsoo.handler.processor.message;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageDispatcher;
import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.handler.message.router.model.RespType;
import com.hbsoo.handler.utils.HttpUtils;
import com.hbsoo.handler.utils.SpringBeanFactory;
import com.hbsoo.msg.annotation.HttpHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.hbsoo.handler.constants.Constants.MSG_TYPE_KEY;

/**
 * Created by zun.wei on 2021/7/30.
 */
@Slf4j
public class HBSHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        log.debug("HBSStringHandler channelRead0 msg --::{}", msg);
        MessageDispatcher.dispatchMsg(ctx, msg, ServerProtocolType.HTTP);

        /*String uri = msg.uri();
        String[] split = uri.split("[?]");
        final List<MessageRouter> handlers = SpringBeanFactory.getBeansOfTypeWithAnnotation(MessageRouter.class, HttpHandler.class);
        boolean b = false;
        for (MessageRouter handler : handlers) {
            final HttpHandler httpHandler = handler.getClass().getAnnotation(HttpHandler.class);
            final String[] values = httpHandler.value();
            for (String value : values) {
                if (value.equals(split[0])) {
                    handler.handler(ctx, msg);
                    b = true;
                }
            }
        }
        if (!b) {
            log.info("http not exist uri handler [{}]", split[0]);
            final DefaultFullHttpResponse response =
                    HttpUtils.resp(null, RespType.HTML, true, HttpResponseStatus.NOT_FOUND).get();
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(MSG_TYPE_KEY).set("http");
        super.exceptionCaught(ctx, cause);
    }
}
