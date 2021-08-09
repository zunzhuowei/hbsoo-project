package com.hbsoo.msg.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by zun.wei on 2021/7/31.
 */
@Data
@Accessors(chain = true)
public class HBSMessage<T> {

    /** 消息头 */
    private MsgHeader header;

    /** content 消息体 */
    private T content;


    public static <T> HBSMessage<T> create(Class<T> contentClazz) {
        final HBSMessage<T> message = new HBSMessage<>();
        MsgHeader header = new MsgHeader();
        message.setHeader(header);
        return message;
    }

    public HBSMessage<T> magicNum(short magicNum) {
        this.header.setMagicNum(magicNum);
        return this;
    }
    public HBSMessage<T> version(short version) {
        this.header.setVersion(version);
        return this;
    }
    public HBSMessage<T> messageType(short msgType) {
        this.header.setMsgType(msgType);
        return this;
    }
    public HBSMessage<T> msgLen(int msgLen) {
        this.header.setMsgLen(msgLen);
        return this;
    }
    public HBSMessage<T> content(T content) {
        this.content = content;
        return this;
    }

}
