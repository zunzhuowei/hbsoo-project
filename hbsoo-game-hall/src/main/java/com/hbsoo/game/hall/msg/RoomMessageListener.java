package com.hbsoo.game.hall.msg;

import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.inner.InnerMessage;
import com.hbsoo.game.inner.InnerMessageDispatcher;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by zun.wei on 2021/8/31.
 */
@Component
public class RoomMessageListener {

    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                final RTopic topic = redissonClient.getTopic(GameConstants.R2H_TOPIC_NAME, new SerializationCodec());
                topic.addListener(InnerMessage.class, (channel, msg) -> {
                    InnerMessageDispatcher.dispatcher(msg);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
