package com.hbsoo.codec.str;

import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by zun.wei on 2021/7/30.
 */
@ChannelHandler.Sharable
public class HBSStringEncoder extends MessageToMessageEncoder<HBSMessage<String>> {


    @Override
    protected void encode(ChannelHandlerContext ctx, HBSMessage<String> msg, List<Object> out) throws Exception {
        final MsgHeader header = msg.getHeader();
        final String content = msg.getContent();
        final short magicNum = header.getMagicNum();
        final short version = header.getVersion();
        final int msgLen = header.getMsgLen();
        final int msgType = header.getMsgType();

        ByteBuf buffer = Unpooled.buffer(msgLen);
        buffer.writeShort(magicNum)
                .writeShort(version)
                .writeInt(msgLen)
                .writeShort(msgType)
                .writeBytes(content.getBytes(StandardCharsets.UTF_8));

        final byte[] datas = buffer.array();
        for (int i = 0; i < datas.length; i++) {
            datas[i] = (byte) (datas[i] << msgType);
        }
        out.add(new String(datas, StandardCharsets.UTF_8));
    }

}
