package com.hbsoo.handler.message.router.adapter;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;

/**
 * protobuf消息处理器
 * Created by zun.wei on 2021/7/31.
 */
public abstract class ProtobufMessageRouterAdapter<T extends GeneratedMessageV3> implements MessageRouter<HBSMessage<byte[]>> {

    protected Channel channel;

    @Override
    public ServerProtocolType getProtocolType() {
        return ServerProtocolType.PROTOBUF;
    }

    @Override
    public void handler(Channel channel, HBSMessage<byte[]> protobufHBSMessage) {
        this.channel = channel;
        final MsgHeader header = protobufHBSMessage.getHeader();
        final int msgType = header.getMsgType();
        final byte[] content = protobufHBSMessage.getContent();

        try {
            final Class<T> v3Class = regMsgClass();
            GeneratedMessageV3 returnObj = (GeneratedMessageV3) v3Class.getDeclaredMethod("getDefaultInstance").invoke(v3Class);
            final Parser<? extends GeneratedMessageV3> parserForType = returnObj.getParserForType();
            final GeneratedMessageV3 v31 = parserForType.parseFrom(content);
            handler(msgType, (T) v31);
        } catch (IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException
                | InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定管道发送消息
     *
     * @param channel 消息管道
     * @param message 消息
     */
    protected void sendMsg(Channel channel, HBSMessage<? extends GeneratedMessageV3> message) {
        channel.writeAndFlush(message);
    }

    /**
     * 当前消息管道发送消息
     *
     * @param message 消息
     */
    protected void sendMsg(HBSMessage<? extends GeneratedMessageV3> message) {
        sendMsg(channel, message);
    }

    /**
     * 处理消息
     *
     * @param msgType 消息类型
     * @param content 消息内容
     */
    protected abstract void handler(int msgType, T content);

    /**
     * 注册需要处理的消息类型class
     */
    protected abstract Class<T> regMsgClass();

}
