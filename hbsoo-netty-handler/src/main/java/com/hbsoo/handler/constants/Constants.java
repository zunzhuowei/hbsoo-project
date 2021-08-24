package com.hbsoo.handler.constants;

import io.netty.util.AttributeKey;

/**
 * Created by zun.wei on 2021/8/3.
 */
public interface Constants {

    /**
     * 消息类型，当前管道中处理的消息类型
     */
    AttributeKey<String> MSG_TYPE_KEY = AttributeKey.valueOf("msgType");

    /**
     * 握手状态
     */
    AttributeKey<Boolean> HANDSHAKE_KEY = AttributeKey.valueOf("isHandshake");

    /**
     * 添加入session manager 的时间
     */
    //AttributeKey<Long> ADD_SESSION_TIME_KEY = AttributeKey.valueOf("addSessionKey");


}
