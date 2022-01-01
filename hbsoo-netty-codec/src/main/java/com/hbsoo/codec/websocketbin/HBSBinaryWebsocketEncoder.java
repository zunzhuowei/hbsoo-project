package com.hbsoo.codec.websocketbin;

import com.google.protobuf.GeneratedMessageV3;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;
import java.util.Objects;

/**
 * Created by zun.wei on 2021/7/30.
 */
@ChannelHandler.Sharable
public class HBSBinaryWebsocketEncoder extends MessageToMessageEncoder<HBSMessage<ByteBuf>> {


    @Override
    protected void encode(ChannelHandlerContext ctx, HBSMessage<ByteBuf> msg, List<Object> out) throws Exception {
        if (msg.getContent() instanceof ByteBuf) {
            final MsgHeader header = msg.getHeader();
            final ByteBuf content = msg.getContent();
            final short magicNum = header.getMagicNum();
            final short version = header.getVersion();
            final int msgLen = header.getMsgLen();
            final int msgType = header.getMsgType();

            ByteBuf buffer = Unpooled.buffer(msgLen);
            buffer.writeShort(magicNum)
                    .writeShort(version)
                    .writeInt(msgLen)
                    .writeShort(msgType);
            //.writeBytes();

            buffer.writeBytes(content);
            BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(buffer);
            out.add(binaryWebSocketFrame);
        } else {
            out.add(msg);
        }
    }

}
