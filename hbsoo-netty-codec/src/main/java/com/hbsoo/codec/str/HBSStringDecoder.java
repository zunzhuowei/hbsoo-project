package com.hbsoo.codec.str;

import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by zun.wei on 2021/7/30.
 */
@Slf4j
@ChannelHandler.Sharable
public class HBSStringDecoder extends MessageToMessageDecoder<ByteBuf> {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        int readableBytes = byteBuf.readableBytes();
        //判断可读消息长度
        if (readableBytes < MsgHeader.HEADER_LENGTH) {
            log.warn("decode message exception, ByteBuf readableBytes is [{}],there is less than header length[{}]",
                    readableBytes, MsgHeader.HEADER_LENGTH);
            return;
        }
        short magicNum = byteBuf.getShort(0);//magicNum
        if (MsgHeader.STR_MAGIC_NUM != magicNum) {
            byteBuf.retain();
            out.add(byteBuf);
            //ctx.channel().closeFuture();
            return;
        }
        short version = byteBuf.getShort(2);//version
        int messageLength = byteBuf.getInt(4);//messageLength
        short messageType = byteBuf.getShort(8);//messageType

        MsgHeader header = new MsgHeader();
        header.setMagicNum(magicNum);
        header.setVersion(version);
        header.setMsgLen(messageLength);
        header.setMsgType(messageType);

        int contentLength = messageLength - MsgHeader.HEADER_LENGTH;
        // 读出缓冲区中的消息头
        byteBuf.skipBytes(MsgHeader.HEADER_LENGTH);

        // 读出 content 消息体
        byte[] datas = new byte[contentLength];
        byteBuf.readBytes(datas);

        HBSMessage<String> message = new HBSMessage<>();
        for (int i = 0; i < datas.length; i++) {
            datas[i] = (byte) (datas[i] >> messageType);
        }
        message.setHeader(header).setContent(new String(datas, StandardCharsets.UTF_8));
        out.add(message);
    }
}
