package com.hbsoo.game.room.processor

import com.hbsoo.game.commons.TestUser
import com.hbsoo.game.inner.InnerMessageProcessor
import com.hbsoo.game.inner.InnerProcessor

/**
 * Created by zun.wei on 2021/9/10.
 *
 */
@InnerProcessor(value = 1)
class Msg1Processor implements InnerMessageProcessor<TestUser> {


    @Override
    Class regMessage() {
        return TestUser.class
    }

    @Override
    void process(TestUser message) {
        println "message = $message"
    }


}
