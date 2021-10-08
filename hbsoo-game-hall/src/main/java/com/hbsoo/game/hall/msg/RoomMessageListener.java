package com.hbsoo.game.hall.msg;

import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.commons.ServerHolder;
import com.hbsoo.game.commons.ServerType;
import com.hbsoo.game.inner.InnerMessage;
import com.hbsoo.game.inner.InnerMessageDispatcher;
import org.redisson.api.*;
import org.redisson.api.listener.PatternStatusListener;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/8/31.
 */
public class RoomMessageListener {

    @Autowired
    private RedissonClient redissonClient;

    @Value("${serverId}")
    private String fromServerId;

    @Autowired
    private ServerHolder serverHolder;

    @PostConstruct
    public void init() {
        serverHolder.saveServerId(ServerType.HALL, fromServerId);
        new Thread(() -> {
            try {
                final RTopic topic = redissonClient.getTopic(GameConstants.R2H_TOPIC_NAME, new SerializationCodec());
                topic.addListener(InnerMessage.class, (channel, msg) -> {
                    final String toServerId = msg.getToServerId();
                    if (toServerId == null || !toServerId.equals(ServerType.HALL + ":" + fromServerId)) {
                        return;
                    }
                    InnerMessageDispatcher.dispatcher(msg);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
