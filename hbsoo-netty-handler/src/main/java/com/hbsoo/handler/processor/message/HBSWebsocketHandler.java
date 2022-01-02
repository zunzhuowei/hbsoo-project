package com.hbsoo.handler.processor.message;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageDispatcher;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import com.hbsoo.msg.model.StrMsgHeader;
import io.netty.buffer.ByteBuf;
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
            final String text = webSocketFrame.text();
            final HBSMessage<String> message = HBSMessage.create(String.class);
            //StrMsgHeader strMsgHeader = new StrMsgHeader();
            //strMsgHeader.setMsgType(Short.MIN_VALUE);
            //message.setHeader(strMsgHeader);
            //message.setContent(text);
            message.messageType(Short.MIN_VALUE).content(text);
            MessageDispatcher.dispatchMsg(ctx.channel(), message, ServerProtocolType.WEBSOCKET_TEXT);
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
            final ByteBuf content = webSocketFrame.content();
            final HBSMessage<ByteBuf> message = HBSMessage.create(ByteBuf.class);
            //MsgHeader msgHeader = new MsgHeader();
            //msgHeader.setMsgType(Short.MIN_VALUE);
            //message.setHeader(msgHeader);
            //message.setContent(content);
            message.messageType(Short.MIN_VALUE).content(content);
            MessageDispatcher.dispatchMsg(ctx.channel(), message, ServerProtocolType.WEBSOCKET_BINARY);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(MSG_TYPE_KEY).set("websocket");
        super.exceptionCaught(ctx, cause);
    }

}
