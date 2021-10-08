package com.hbsoo.game.inner;

import com.alibaba.fastjson.JSON;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by zun.wei on 2021/9/14.
 */
public class MessageInformer {

    @Autowired
    private RedissonClient redissonClient;

    @Value("${serverId}")
    private String fromServerId;

    /**
     * 发送消息到队列中
     *
     * @param toServerId   发送目的地的服务器id
     * @param topicName    队列主题
     * @param msgType      消息类型
     * @param delaySeconds 延迟时间
     * @param async        是否异步发送
     * @param isArrJson    是否为列表对象
     * @param msgObj       消息对象
     */
    public void send(String toServerId, String topicName, int msgType,
                     Long delaySeconds, boolean async, boolean isArrJson, Object msgObj) {
        final String json = JSON.toJSONString(msgObj);
        final RTopic topic = redissonClient.getTopic(topicName, new SerializationCodec());
        final InnerMessage message = new InnerMessage(delaySeconds);
        message.setMsgType(msgType);
        message.setDataJson(json);
        message.setBatch(isArrJson);
        message.setToServerId(toServerId);
        message.setFromServerId(fromServerId);
        if (async) {
            topic.publishAsync(message);
        } else {
            topic.publish(message);
        }
    }


}
