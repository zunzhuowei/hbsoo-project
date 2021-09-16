package com.hbsoo.game.hall.msg;

import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.inner.InnerMessage;
import com.hbsoo.game.inner.MessageInformer;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by zun.wei on 2021/8/31.
 */
public class RoomMessageInformer {

    @Autowired
    private MessageInformer messageInformer;

    @Value("${serverId}")
    private String fromServerId;

    public void send(String serverId, int msgType, Object msgObj) {
        send(serverId, msgType, 0L, false, false, msgObj);
    }

    public void send(String serverId, int msgType, Long delaySeconds, Object msgObj) {
        send(serverId, msgType, delaySeconds, false, false, msgObj);
    }

    public void send(String toServerId, int msgType, Long delaySeconds, boolean async, boolean isArrJson, Object msgObj) {
        messageInformer.send(toServerId, fromServerId, GameConstants.H2R_TOPIC_NAME, msgType, delaySeconds, async, isArrJson, msgObj);
    }

}
