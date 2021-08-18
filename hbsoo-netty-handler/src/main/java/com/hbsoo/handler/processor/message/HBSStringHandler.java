package com.hbsoo.handler.processor.message;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageDispatcher;
import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.handler.message.router.model.MessageTask;
import com.hbsoo.handler.utils.SpringBeanFactory;
import com.hbsoo.msg.annotation.StrHandler;
import com.hbsoo.msg.model.HBSMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.hbsoo.handler.constants.Constants.MSG_TYPE_KEY;

/**
 * Created by zun.wei on 2021/7/30.
 */
@Slf4j
public class HBSStringHandler extends SimpleChannelInboundHandler<HBSMessage<String>> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HBSMessage<String> msg) throws Exception {
        log.debug("HBSStringHandler channelRead0 msg --::{}", msg);
        MessageDispatcher.dispatchMsg(
                new MessageTask()
                        .setChannel(ctx.channel())
                        .setProtocolType(ServerProtocolType.STRING)
                        .setMsg(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(MSG_TYPE_KEY).set("string");
        super.exceptionCaught(ctx, cause);
    }
}
