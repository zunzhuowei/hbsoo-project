package com.hbsoo.handler.message.router.adapter;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.nio.charset.StandardCharsets;

/**
 * protobuf消息处理器
 * Created by zun.wei on 2021/7/31.
 */
public abstract class BinaryWebsocketMessageRouterAdapter implements MessageRouter<HBSMessage<ByteBuf>> {

    protected Channel channel;

    @Override
    public ServerProtocolType getProtocolType() {
        return ServerProtocolType.WEBSOCKET_BINARY;
    }

    @Override
    public void handler(Channel channel, HBSMessage<ByteBuf> protobufHBSMessage) {
        this.channel = channel;
        final MsgHeader header = protobufHBSMessage.getHeader();
        final int msgType = header.getMsgType();
        final ByteBuf content = protobufHBSMessage.getContent();
        final byte[] array = content.array();
        String json = new String(array, StandardCharsets.UTF_8);
        handler(msgType, json);
    }

    /**
     * 指定管道发送消息
     *
     * @param channel 消息管道
     * @param message 消息
     */
    protected void sendMsg(Channel channel, HBSMessage<ByteBuf> message) {
        channel.writeAndFlush(message);
    }

    /**
     * 当前消息管道发送消息
     *
     * @param message 消息
     */
    protected void sendMsg(HBSMessage<ByteBuf> message) {
        sendMsg(channel, message);
    }

    /**
     * 处理消息
     *
     * @param msgType 消息类型
     * @param content 消息内容
     */
    protected abstract void handler(int msgType, String content);

    /**
     * 注册需要处理的消息类型class
     */
    //protected abstract Class<T> regMsgClass();

}
