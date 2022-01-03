package com.hbsoo.codec.websockettext;

import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * Created by zun.wei on 2021/7/30.
 */
@ChannelHandler.Sharable
public class HBSTextWebsocketEncoder extends MessageToMessageEncoder<HBSMessage<String>> {


    @Override
    protected void encode(ChannelHandlerContext ctx, HBSMessage<String> msg, List<Object> out) throws Exception {
        if (msg.getContent() instanceof String) {
            final String content = msg.getContent();

            //浏览器 Could not decode a text frame as UTF-8.

            final MsgHeader header = msg.getHeader();
            final short magicNum = header.getMagicNum();
            final short version = header.getVersion();
            final int msgLen = header.getMsgLen();
            final int msgType = header.getMsgType();

            /*ByteBuf buffer = Unpooled.buffer(msgLen);
            buffer.writeShort(magicNum)
                    .writeShort(version)
                    .writeInt(msgLen)
                    .writeShort(msgType);*/

            StringBuilder sb = new StringBuilder();
            sb
            .append("{")
            .append("\"").append("magicNum").append("\"").append(":").append(magicNum).append(",")
            .append("\"").append("version").append("\"").append(":").append(version).append(",")
            .append("\"").append("msgLen").append("\"").append(":").append(msgLen).append(",")
            .append("\"").append("msgType").append("\"").append(":").append(msgType).append(",")
            .append("\"").append("data").append("\"").append(":").append(content)
            .append("}");

            ByteBuf buffer = Unpooled.buffer(sb.length());
            buffer.writeBytes(sb.toString().getBytes());
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(buffer);
            out.add(textWebSocketFrame);
        } else {
            out.add(msg);
        }
    }

}
