package com.hbsoo.codec.str;

import com.hbsoo.codec.http.Constants;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import com.hbsoo.msg.model.StrMsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Created by zun.wei on 2021/7/30.
 */
@Slf4j
@ChannelHandler.Sharable
public class HBSStringDecoder extends MessageToMessageDecoder<ByteBuf> {



    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        final Boolean isHandshake = ctx.channel().attr(Constants.HANDSHAKE_KEY).get();
        if (Objects.isNull(isHandshake) || !isHandshake) {
            log.error("HBSStringDecoder channel is not handshaker");
            ctx.channel().close();
            byteBuf.release();
            return;
        }
        int readableBytes = byteBuf.readableBytes();
        //判断可读消息长度
        if (readableBytes < MsgHeader.HEADER_LENGTH) {
            log.warn("decode message exception, ByteBuf readableBytes is [{}],there is less than header length[{}]",
                    readableBytes, MsgHeader.HEADER_LENGTH);
            return;
        }
        short magicNum = byteBuf.getShort(0);//magicNum
        // 如果不是定义的字符串类型消息，则往下抛
        if (StrMsgHeader.STR_MAGIC_NUM != magicNum) {
            byteBuf.retain();
            out.add(byteBuf);
            return;
        }
        short version = byteBuf.getShort(2);//version
        int messageLength = byteBuf.getInt(4);//messageLength
        short messageType = byteBuf.getShort(8);//messageType
        int contentLength = messageLength - MsgHeader.HEADER_LENGTH;
        // 消息长度，不处理
        if (contentLength < 0) {
            byteBuf.retain();
            out.add(byteBuf);
            return;
        }
        // 缓冲区可读小于消息长度
        if (byteBuf.readableBytes() < messageLength) {
            return;
        }

        /*MsgHeader header = new MsgHeader();
        header.setMagicNum(magicNum);
        header.setVersion(version);
        header.setMsgLen(contentLength);
        header.setMsgType(messageType);*/

        // 读出缓冲区中的消息头
        byteBuf.skipBytes(MsgHeader.HEADER_LENGTH);

        // 读出 content 消息体
        byte[] datas = new byte[contentLength];
        byteBuf.readBytes(datas);

        final HBSMessage<String> message= HBSMessage.create(String.class);
        //HBSMessage<String> message = new HBSMessage<>();
        for (int i = 0; i < datas.length; i++) {
            datas[i] = (byte) (datas[i] ^ version);
        }
        message.magicNum(magicNum).version(version)
                .msgLen(contentLength).messageType(messageType)
                .content(new String(datas, StandardCharsets.UTF_8));
        //message.setHeader(header).setContent(new String(datas, StandardCharsets.UTF_8));
        out.add(message);
    }
}
