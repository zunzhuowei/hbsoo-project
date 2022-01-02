package com.hbsoo.handler.processor.message;

import com.hbsoo.handler.message.router.model.RespType;
import com.hbsoo.handler.utils.HttpUtils;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.StrMsgHeader;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.hbsoo.handler.constants.Constants.MSG_TYPE_KEY;

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
        final String msgType = ctx.channel().attr(MSG_TYPE_KEY).get();
        // http 消息
        if (Objects.equals(msgType, "http")) {
            final String message = cause.getMessage();
            byte[] bytes = (Objects.nonNull(message) ? message : "").getBytes(StandardCharsets.UTF_8);
            final DefaultFullHttpResponse response =
                    HttpUtils.resp(bytes, RespType.HTML, true, HttpResponseStatus.OK).get();
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        // string 协议消息
        if (Objects.equals(msgType, "string")) {
            HBSMessage<String> message = HBSMessage.create(String.class);
            //final StrMsgHeader header = new StrMsgHeader();
            String errMsg = cause.getMessage();
            errMsg = Objects.isNull(errMsg) ? "" : errMsg;
            //header.setMsgLen(errMsg.getBytes().length);
            message.content(errMsg);
            ctx.channel().writeAndFlush(message);
            return;
        }
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
