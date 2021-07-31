package com.hbsoo.handler.message;

import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.handler.utils.SpringBeanFactory;
import com.hbsoo.msg.annotation.StrHandler;
import com.hbsoo.msg.model.HBSMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by zun.wei on 2021/7/30.
 */
@Slf4j
public class HBSStringHandler extends SimpleChannelInboundHandler<HBSMessage<String>> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HBSMessage<String> msg) throws Exception {
        log.debug("HBSStringHandler channelRead0 msg --::{}", msg);
        final List<MessageRouter> handlers = SpringBeanFactory.getBeansOfTypeWithAnnotation(MessageRouter.class, StrHandler.class);
        for (MessageRouter handler : handlers) {
            final StrHandler httpHandler = handler.getClass().getAnnotation(StrHandler.class);
            final int value = httpHandler.value();
            final short msgType = msg.getHeader().getMsgType();
            if (value == msgType) {
                handler.handler(ctx, msg);
            }
        }
    }


}
