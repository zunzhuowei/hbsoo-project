package com.hbsoo.client

import com.hbsoo.handler.message.router.adapter.StringMessageRouterAdapter
import com.hbsoo.msg.annotation.StrHandler
import com.hbsoo.msg.model.HBSMessage
import groovy.util.logging.Slf4j

/**
 * Created by zun.wei on 2021/8/10.
 *
 */
@Slf4j
@StrHandler([10])
class StringMsgType10Handler extends StringMessageRouterAdapter {


    @Override
    protected HBSMessage<String> handler(int msgType, String content) {
        log.info("get from server msgType,content {},{}", msgType, content)
        return null
    }

}
