package com.hbsoo.game.inner;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by zun.wei on 2021/9/9.
 */
public interface InnerMessageProcessor<T> {


    Class<T> regMessage();

    /**
     * 处理消息
     * @param message 游戏消息
     */
    default void process(T message){
        final String s = JSON.toJSONString(message);
        System.err.println("Message not processed --:" + s);
    }

    default void process(List<T> messages){
        final String s = JSON.toJSONString(messages);
        System.err.println("Message not processed --:" + s);
    }

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
