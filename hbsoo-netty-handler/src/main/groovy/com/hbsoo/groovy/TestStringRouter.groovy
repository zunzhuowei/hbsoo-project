package com.hbsoo.groovy

import com.hbsoo.handler.message.router.StringAbstractHandler
import com.hbsoo.msg.annotation.StrHandler
import com.hbsoo.msg.model.HBSMessage
import com.hbsoo.msg.model.StrMsgHeader

/**
 * Created by zun.wei on 2021/7/31.
 *
 */
@StrHandler(value = [1])
class TestStringRouter extends StringAbstractHandler {


    @Override
    protected HBSMessage<String> handler(int msgType, String content) {
        HBSMessage<String> message = new HBSMessage<>()
        message.content = "haha"

        StrMsgHeader header = new StrMsgHeader()
        header.magicNum = header.STR_MAGIC_NUM
        header.msgType = 1 as short
        header.setVersion(1 as short)
        header.setMsgLen(message.content.getBytes().length)
        message.header = header
        message
    }


}
