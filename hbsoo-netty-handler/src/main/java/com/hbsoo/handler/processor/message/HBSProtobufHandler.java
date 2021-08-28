package com.hbsoo.handler.processor.message;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageDispatcher;
import com.hbsoo.handler.message.router.model.MessageTask;
import com.hbsoo.msg.model.HBSMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import static com.hbsoo.handler.constants.Constants.MSG_TYPE_KEY;

/**
 * Created by zun.wei on 2021/7/30.
 */
@Slf4j
public class HBSProtobufHandler extends SimpleChannelInboundHandler<HBSMessage<byte[]>> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HBSMessage<byte[]> msg) throws Exception {
        log.debug("HBSProtobufHandler channelRead0 msg --::{}", msg);
        MessageDispatcher.dispatchMsg(
                new MessageTask()
                        .setChannel(ctx.channel())
                        .setProtocolType(ServerProtocolType.PROTOBUF)
                        .setMsg(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(MSG_TYPE_KEY).set("protobuf");
        super.exceptionCaught(ctx, cause);
    }
}
