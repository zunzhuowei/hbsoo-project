package com.hbsoo.handler.message.router.adapter;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.channel.Channel;

/**
 * 字符串消息处理器
 * Created by zun.wei on 2021/7/31.
 */
public abstract class TextWebsocketMessageRouterAdapter implements MessageRouter<HBSMessage<String>> {

    //protected Channel channel;

    @Override
    public ServerProtocolType getProtocolType() {
        return ServerProtocolType.WEBSOCKET_TEXT;
    }

    @Override
    public void handler(Channel channel, HBSMessage<String> stringHBSMessage) {
        final MsgHeader header = stringHBSMessage.getHeader();
        final int msgType = header.getMsgType();
        final String content = stringHBSMessage.getContent();
        handler(channel,msgType, content);
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
     * 处理消息
     *
     * @param msgType 消息类型
     * @param content 消息内容
     */
    protected abstract void handler(Channel channel, int msgType, String content);

}
