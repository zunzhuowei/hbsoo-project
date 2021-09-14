package com.hbsoo.game.inner;

import com.alibaba.fastjson.JSON;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zun.wei on 2021/9/14.
 */
public class MessageInformer {

    @Autowired
    private RedissonClient redissonClient;


    public void send(String topicName, int msgType, Long delaySeconds, boolean async, boolean isArrJson, Object msgObj) {
        final String json = JSON.toJSONString(msgObj);
        final RTopic topic = redissonClient.getTopic(topicName, new SerializationCodec());
        final InnerMessage message = new InnerMessage(delaySeconds);
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
