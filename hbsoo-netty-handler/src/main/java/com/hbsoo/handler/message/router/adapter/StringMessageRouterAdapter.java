package com.hbsoo.handler.message.router.adapter;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageDispatcher;
import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

/**
 * 字符串消息处理器
 * Created by zun.wei on 2021/7/31.
 */
public abstract class StringMessageRouterAdapter implements MessageRouter<HBSMessage<String>> {

    protected Channel channel;

    @Override
    public ServerProtocolType getProtocolType() {
        return ServerProtocolType.STRING;
    }

    @Override
    public void handler(Channel channel, HBSMessage<String> stringHBSMessage) {
        this.channel = channel;
        final MsgHeader header = stringHBSMessage.getHeader();
        final int msgType = header.getMsgType();
        final String content = stringHBSMessage.getContent();
        handler(msgType, content);
    }

    /**
     * 指定管道发送消息
     * @param channel 消息管道
     * @param message 消息
     */
    protected void sendMsg(Channel channel, HBSMessage<String> message) {
        channel.writeAndFlush(message);
    }

    /**
     * 当前消息管道发送消息
     * @param message 消息
     */
    protected void sendMsg(HBSMessage<String> message) {
        sendMsg(channel, message);
    }

    /**
     * 处理消息
     * @param msgType 消息类型
     * @param content 消息内容
     */
    protected abstract void handler(int msgType, String content);

}
