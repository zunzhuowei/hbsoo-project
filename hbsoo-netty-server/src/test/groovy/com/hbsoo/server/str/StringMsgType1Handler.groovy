package com.hbsoo.server.str

import com.hbsoo.handler.message.router.adapter.StringMessageRouterAdapter
import com.hbsoo.msg.annotation.StrHandler
import com.hbsoo.msg.model.HBSMessage
import com.hbsoo.msg.model.StrMsgHeader
import groovy.util.logging.Slf4j

/**
 * Created by zun.wei on 2021/8/9.
 *
 */
@Slf4j
@StrHandler([0, 1, 2])
class StringMsgType1Handler extends StringMessageRouterAdapter{


    @Override
    protected HBSMessage<String> handler(int msgType, String content) {
        log.info("server StringMsgType1Handler msgType content --::{},{}", msgType, content)
        String ok = "ok"
        HBSMessage<String> resp = HBSMessage.create(String.class).magicNum(StrMsgHeader.STR_MAGIC_NUM).version(1 as short).messageType(10 as short).msgLen(ok.bytes.length).content(ok)
        //TODO 需要修改成可以推送多条消息
        return resp
    }

}
