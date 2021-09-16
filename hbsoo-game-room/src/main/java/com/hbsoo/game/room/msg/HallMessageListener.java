package com.hbsoo.game.room.msg;

import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.inner.InnerMessage;
import com.hbsoo.game.inner.InnerMessageDispatcher;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/8/31.
 */
public class HallMessageListener {


    @Autowired
    private RedissonClient redissonClient;

    @Value("${serverId}")
    private String fromServerId;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                final RTopic topic = redissonClient.getTopic(GameConstants.H2R_TOPIC_NAME, new SerializationCodec());
                topic.addListener(InnerMessage.class, (channel, msg) -> {
                    final String toServerId = msg.getToServerId();
                    if (toServerId == null || !toServerId.equals(fromServerId)) {
                        return;
                    }
                    InnerMessageDispatcher.dispatcher(msg);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    final RMap<Object, Object> map = redissonClient.getMap(GameConstants.ROOM_SERVER_NAME_MAP);
                    map.put(fromServerId, System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
