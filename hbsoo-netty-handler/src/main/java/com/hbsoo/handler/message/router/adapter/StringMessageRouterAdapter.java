package com.hbsoo.handler.message.router.adapter;

import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.msg.model.MsgHeader;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

/**
 * 字符串消息处理器
 * Created by zun.wei on 2021/7/31.
 */
public abstract class StringMessageRouterAdapter implements MessageRouter<HBSMessage<String>> {

    @Override
    public void handler(ChannelHandlerContext ctx, HBSMessage<String> stringHBSMessage) {
        final MsgHeader header = stringHBSMessage.getHeader();
        final int msgType = header.getMsgType();
        final String content = stringHBSMessage.getContent();
        final HBSMessage<String> response = handler(msgType, content);
        if (Objects.nonNull(response)) {
            ctx.writeAndFlush(response);
        }
    }

    /**
     * 处理消息
     * @param msgType 消息类型
     * @param content 消息内容
     * @return 消息返回值
     */
    protected abstract HBSMessage<String> handler(int msgType, String content);

}
