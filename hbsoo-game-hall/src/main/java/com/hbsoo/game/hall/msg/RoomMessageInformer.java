package com.hbsoo.game.hall.msg;

import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.inner.InnerMessage;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zun.wei on 2021/8/31.
 */
@Component
public class RoomMessageInformer {

    @Autowired
    private RedissonClient redissonClient;


    public void send(int msgType, String jsonObj) {
        send(msgType, false, false, jsonObj);
    }

    public void sendAsync(int msgType, String jsonObj) {
        send(msgType, true, false, jsonObj);
    }

    public void send(int msgType, boolean isArrJson, String jsonObj) {
        send(msgType, false, isArrJson, jsonObj);
    }

    public void sendAsync(int msgType, boolean isArrJson, String jsonObj) {
        send(msgType, true, isArrJson, jsonObj);
    }

    public void send(int msgType, boolean async, boolean isArrJson, String json) {
        final RTopic topic = redissonClient.getTopic(GameConstants.H2R_TOPIC_NAME, new SerializationCodec());
        final InnerMessage message = new InnerMessage();
        message.setMsgType(msgType);
        message.setDataJson(json);
        message.setBatch(isArrJson);
        if (async) {
            topic.publishAsync(message);
        } else {
            topic.publish(message);
        }
    }

}
