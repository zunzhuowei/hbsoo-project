package com.hbsoo.handler.processor.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

import static com.hbsoo.handler.constants.Constants.MSG_TYPE_KEY;

/**
 * Created by zun.wei on 2021/8/18.
 */
@Slf4j
public class HBSWebsocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame webSocketFrame = (TextWebSocketFrame) msg;
            webSocketFrame.text();

        }
        if (msg instanceof PingWebSocketFrame) {
            PingWebSocketFrame webSocketFrame = (PingWebSocketFrame) msg;

        }
        if (msg instanceof PongWebSocketFrame) {
            PongWebSocketFrame webSocketFrame = (PongWebSocketFrame) msg;

        }
        if (msg instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame webSocketFrame = (CloseWebSocketFrame) msg;

        }
        if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame webSocketFrame = (BinaryWebSocketFrame) msg;
            webSocketFrame.content();

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(MSG_TYPE_KEY).set("websocket");
        super.exceptionCaught(ctx, cause);
    }

}
