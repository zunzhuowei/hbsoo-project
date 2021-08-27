package com.hbsoo.codec.websocketprotobuf;

import com.google.protobuf.GeneratedMessageV3;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by zun.wei on 2021/7/30.
 */
@ChannelHandler.Sharable
public class HBSWebsocketProtobufEncoder extends MessageToMessageEncoder<HBSMessage<? extends GeneratedMessageV3>> {


    @Override
    protected void encode(ChannelHandlerContext ctx, HBSMessage<? extends GeneratedMessageV3> msg, List<Object> out) throws Exception {
        final MsgHeader header = msg.getHeader();
        final GeneratedMessageV3 content = msg.getContent();
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

        buffer.writeBytes(content.toByteArray());
        out.add(buffer);
    }

}
