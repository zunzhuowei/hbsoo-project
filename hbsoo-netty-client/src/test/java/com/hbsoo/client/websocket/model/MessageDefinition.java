package com.hbsoo.client.websocket.model;

import com.google.protobuf.GeneratedMessageV3;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by zun.wei on 2021/7/18.
 * 消息定义
 */
@Data
@Accessors(chain = true)
public class MessageDefinition<T extends GeneratedMessageV3> {

    /**
     * 消息编号
     */
    private String msgNo;

    /**
     * 消息
     */
    private T message;


}
