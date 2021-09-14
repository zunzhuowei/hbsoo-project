package com.hbsoo.game.room.msg;

import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.inner.InnerMessage;
import com.hbsoo.game.inner.MessageInformer;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zun.wei on 2021/8/31.
 */
public class HallMessageInformer {

    @Autowired
    private MessageInformer messageInformer;


    public void send(int msgType, Object msgObj) {
        send(msgType, 0L, false, false, msgObj);
    }

    public void send(int msgType, Long delaySeconds, Object msgObj) {
        send(msgType, delaySeconds, false, false, msgObj);
    }

    public void send(int msgType, Long delaySeconds, boolean async, boolean isArrJson, Object msgObj) {
        messageInformer.send(GameConstants.R2H_TOPIC_NAME, msgType, delaySeconds, async, isArrJson, msgObj);
    }

}
