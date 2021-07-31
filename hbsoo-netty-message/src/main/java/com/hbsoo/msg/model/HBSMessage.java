package com.hbsoo.msg.model;

import com.hbsoo.msg.model.MsgHeader;
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


}
