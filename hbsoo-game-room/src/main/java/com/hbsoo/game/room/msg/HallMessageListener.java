package com.hbsoo.game.room.msg;

import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.commons.GameMessage;
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
public class HallMessageListener {


    @Autowired
    private RedissonClient redissonClient;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                final RTopic topic = redissonClient.getTopic(GameConstants.H2R_TOPIC_NAME, new SerializationCodec());
                topic.addListener(GameMessage.class, (channel, msg) -> {
                    System.out.println("channel = " + channel);
                    System.out.println("msg = " + msg);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


}
