package com.hbsoo.game.inner;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by zun.wei on 2021/9/9.
 */
public interface InnerMessageProcessor<T> {

    /**
     * 注册要处理的消息类型
     */
    Class<T> regMessage();

    /**
     * 处理单个消息
     * @param message 消息
     */
    default void process(T message){
        final String s = JSON.toJSONString(message);
        System.err.println("Message not processed --:" + s);
    }

    /**
     * 处理一组消息
     * @param messages 消息列表
     */
    default void process(List<T> messages){
        final String s = JSON.toJSONString(messages);
        System.err.println("Message not processed --:" + s);
    }

    /**
     * 处理原始消息
     * @param batch 是否为批量消息
     * @param dataJson 数据json字符串
     */
    default void process0(boolean batch, String dataJson) {
        if (batch) {
            final List<T> ts = JSON.parseArray(dataJson, regMessage());
            process(ts);
            return;
        }
        final T t = JSON.parseObject(dataJson, regMessage());
        process(t);
    }

}
