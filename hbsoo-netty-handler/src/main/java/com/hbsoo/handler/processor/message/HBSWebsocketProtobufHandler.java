package com.hbsoo.handler.processor.message;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageDispatcher;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import com.hbsoo.msg.model.ProtobufMsgHeader;
import com.hbsoo.msg.model.WebsocketProtobufMsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.hbsoo.handler.constants.Constants.MSG_TYPE_KEY;

/**
 * Created by zun.wei on 2021/8/18.
 */
@Slf4j
public class HBSWebsocketProtobufHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    static AttributeKey<Boolean> HANDSHAKE_KEY = AttributeKey.valueOf("isHandshake");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame webSocketFrame) throws Exception {
        final ByteBuf byteBuf = webSocketFrame.content();
        final Boolean isHandshake = ctx.channel().attr(HANDSHAKE_KEY).get();
        if (Objects.isNull(isHandshake) || !isHandshake) {
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
        if (WebsocketProtobufMsgHeader.WEBSOCKET_PROTOBUF_MAGIC_NUM != magicNum) {
            return;
        }
        short version = byteBuf.getShort(2);//version
        int messageLength = byteBuf.getInt(4);//messageLength
        short messageType = byteBuf.getShort(8);//messageType
        int contentLength = messageLength - MsgHeader.HEADER_LENGTH;
        // 消息长度，不处理
        if (contentLength < 0) {
            return;
        }
        // 缓冲区可读小于消息长度
        if (byteBuf.readableBytes() < messageLength) {
            return;
        }

        MsgHeader header = new MsgHeader();
        header.setMagicNum(magicNum);
        header.setVersion(version);
        header.setMsgLen(contentLength);
        header.setMsgType(messageType);

        // 读出缓冲区中的消息头
        byteBuf.skipBytes(MsgHeader.HEADER_LENGTH);

        // 读出 content 消息体
        byte[] datas = new byte[contentLength];
        byteBuf.readBytes(datas);

        HBSMessage<byte[]> message = new HBSMessage<>();
        message.setHeader(header).setContent(datas);
        MessageDispatcher.dispatchMsg(ctx.channel(), message, ServerProtocolType.WEBSOCKET_PROTOBUF);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().attr(MSG_TYPE_KEY).set("websocketProtobuf");
        super.exceptionCaught(ctx, cause);
    }

}
