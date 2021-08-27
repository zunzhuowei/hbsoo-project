package com.hbsoo.msg.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by zun.wei on 2021/7/31.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public final class WebsocketProtobufMsgHeader extends MsgHeader {

    /** 字符串魔法头 */
    public static final short WEBSOCKET_PROTOBUF_MAGIC_NUM = 0xf9d;

    @Override
    public short getVersion() {
        return 1;
    }

    @Override
    public int getMsgLen() {
        return super.getMsgLen();
    }

    @Override
    public short getMagicNum() {
        return WEBSOCKET_PROTOBUF_MAGIC_NUM;
    }


}
