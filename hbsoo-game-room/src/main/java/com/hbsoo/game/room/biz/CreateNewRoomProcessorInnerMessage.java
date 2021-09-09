package com.hbsoo.game.room.biz;

import com.hbsoo.game.commons.InnerMessage;
import com.hbsoo.game.commons.InnerMessageProcessor;

/**
 * Created by zun.wei on 2021/8/31.
 */
public class CreateNewRoomProcessorInnerMessage implements InnerMessageProcessor {


    @Override
    public void process(InnerMessage message) {
        message.getDataJson();
        message.getMsgType();
    }

}
