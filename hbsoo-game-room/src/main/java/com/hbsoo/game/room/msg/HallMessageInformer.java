package com.hbsoo.game.room.msg;

import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.inner.MessageInformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by zun.wei on 2021/8/31.
 */
public class HallMessageInformer {

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
        messageInformer.send(toServerId, fromServerId, GameConstants.R2H_TOPIC_NAME, msgType, delaySeconds, async, isArrJson, msgObj);
    }

}
