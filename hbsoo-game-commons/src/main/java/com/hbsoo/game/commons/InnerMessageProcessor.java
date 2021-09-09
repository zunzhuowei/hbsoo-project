package com.hbsoo.game.commons;

/**
 * Created by zun.wei on 2021/9/9.
 */
public interface InnerMessageProcessor {

    /**
     * 处理消息
     * @param message 游戏消息
     */
    void process(InnerMessage message);

}
