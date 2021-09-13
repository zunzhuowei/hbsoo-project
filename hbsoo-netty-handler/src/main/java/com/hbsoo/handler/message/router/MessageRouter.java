package com.hbsoo.handler.message.router;

import com.hbsoo.handler.constants.ServerProtocolType;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 *  消息处理器
 * Created by zun.wei on 2021/7/31.
 */
public interface MessageRouter<MSG> {

    /**
     * 处理 netty 管道消息
     * @param channel 消息管道
     * @param msg 消息类
     */
    void handler(Channel channel, MSG msg);

    /**
     * 消息协议类型
     */
    ServerProtocolType getProtocolType();

    /**
     * 获取属性值
     * @param channel 管道
     * @param key 属性名
     * @param <T> 属性值类型
     * @return 属性值
     */
    default <T> T getAttr(Channel channel,String key) {
        return (T) channel.attr(AttributeKey.valueOf(key)).get();
    }

    /**
     * 设置属性值
     * @param channel 管道
     * @param key 属性名
     * @param t 属性值
     * @param <T> 属性类型
     */
    default <T> void setAttr(Channel channel,String key, T t){
        channel.attr(AttributeKey.valueOf(key)).set(t);
    }

    /**
     * 转发消息
     * @param channel 转发到哪个消息管道
     * @param msg 消息内容
     */
    default void forward(Channel channel, MSG msg){
        MessageDispatcher.dispatchMsg(channel, msg, getProtocolType());
    }

    /**
     * 转发消息 带延迟时间（秒）
     * @param delaySecond 延迟时间
     * @param channel 转发到哪个消息管道
     * @param msg 消息内容
     */
    default void forward(long delaySecond, Channel channel, MSG msg){
        MessageDispatcher.dispatchMsg(delaySecond, channel, msg, getProtocolType());
    }



}
