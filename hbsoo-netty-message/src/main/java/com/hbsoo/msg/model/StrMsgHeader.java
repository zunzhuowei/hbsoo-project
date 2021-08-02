package com.hbsoo.msg.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by zun.wei on 2021/7/31.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public final class StrMsgHeader extends MsgHeader {

    /** 字符串魔法头 */
    public static final short STR_MAGIC_NUM = 0xf9f;

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
        return STR_MAGIC_NUM;
    }


}
