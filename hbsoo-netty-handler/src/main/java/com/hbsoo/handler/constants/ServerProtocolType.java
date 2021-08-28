package com.hbsoo.handler.constants;

/**
 * Created by zun.wei on 2021/7/31.
 */
public enum ServerProtocolType {

    /**
     * 字符串
     */
    STRING,
    /**
     * protobuf
     */
    PROTOBUF,
    /**
     * websocket
     */
    WEBSOCKET,
    /**
     * websocket binary
     */
    WEBSOCKET_BINARY,
    /**
     * websocket text 文本
     */
    WEBSOCKET_TEXT,
    /**
     * http 协议
     */
    HTTP,
    /**
     * websocket 包装 protobuf协议
     */
    WEBSOCKET_PROTOBUF,
    /**
     * 字节流
     */
    BYTE_ARRAY,
    /**
     * 握手协议
     */
    HANDSHAKER,
    ;

}
