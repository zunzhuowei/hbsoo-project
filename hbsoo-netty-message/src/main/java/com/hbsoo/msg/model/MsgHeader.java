package com.hbsoo.msg.model;

import lombok.Data;

/**
 * Created by zun.wei on 2021/7/31.
 */
@Data
public final class MsgHeader {

    /**
     * 消息头长度
     */
    public static final short HEADER_LENGTH = 10;
    /** 字符串魔法头 */
    public static final short STR_MAGIC_NUM = 0xf9f;

    /**
     * 魔法字段
     */
    private short magicNum;
    /**
     * 版本号
     */
    private short version;
    /**
     * 消息长度
     */
    private int msgLen;
    /**
     * 消息类型
     */
    private short msgType;

    public void setMsgLen(int msgType) {
        this.msgLen = msgType + HEADER_LENGTH;
    }


}
