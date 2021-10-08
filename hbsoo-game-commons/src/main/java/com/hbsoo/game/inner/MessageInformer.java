package com.hbsoo.game.inner;

import com.alibaba.fastjson.JSON;
import com.hbsoo.game.commons.GameConstants;
import com.hbsoo.game.commons.ServerHolder;
import com.hbsoo.game.commons.ServerType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by zun.wei on 2021/9/14.
 */
@Slf4j
public class MessageInformer {

    @Autowired
    private RedissonClient redissonClient;

    @Value("${serverId}")
    private String fromServerId;

    @Autowired
    private ServerHolder serverHolder;

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

    /**
     * 发送给指定服务器类型的玩家
     * @param serverType 发送目的的服务器类型
     * @param playerId 玩家id
     * @param msgType 消息类型
     * @param delaySeconds 延迟时间
     * @param async 是否异步发送
     * @param isArrJson 是否为列表对象
     * @param msgObj 消息对象
     */
    public void send(ServerType serverType, Long playerId,
                     int msgType, Long delaySeconds,
                     boolean async, boolean isArrJson, Object msgObj) {
        final String serverId = serverHolder.getServerId(playerId, serverType);
        if (!StringUtils.hasLength(serverId)) {
            log.warn("MessageInformer send serverId is null");
            return;
        }
        String topicName = serverType == ServerType.HALL ? GameConstants.H2R_TOPIC_NAME : GameConstants.R2H_TOPIC_NAME;
        send(serverId, topicName, msgType, delaySeconds, async, isArrJson, msgObj);
    }

    /**
     * 登录服务器
     * @param serverType 服务器类型
     * @param playerId 玩家id
     * @param msgType 消息类型
     * @param delaySeconds 延迟时间
     * @param async 是否异步发送
     * @param isArrJson 是否为列表对象
     * @param msgObj 消息对象
     */
    public void loginServer(ServerType serverType, Long playerId,
                                    int msgType, Long delaySeconds,
                                    boolean async, boolean isArrJson, Object msgObj) {
        String serverId = serverHolder.getServerId(playerId, serverType);
        if (!StringUtils.hasLength(serverId)) {
            final List<String> serverIds = serverHolder.getServerIds(serverType);
            if (Objects.isNull(serverIds) || serverIds.isEmpty()) {
                log.warn("MessageInformer send serverIds is null");
                return;
            }
            Random random = new Random();
            int next = random.nextInt(serverIds.size());
            serverId = serverIds.get(next);
        }
        String topicName = serverType == ServerType.HALL ? GameConstants.H2R_TOPIC_NAME : GameConstants.R2H_TOPIC_NAME;
        send(serverId, topicName, msgType, delaySeconds, async, isArrJson, msgObj);
    }

    /**
     * 发送给指定的服务器类型的服务器
     * @param serverType 发送目的的服务器类型
     * @param msgType 消息类型
     * @param delaySeconds 延迟时间
     * @param async 是否异步发送
     * @param isArrJson 是否为列表对象
     * @param msgObj 消息对象
     */
    public void send(ServerType serverType,
                     int msgType, Long delaySeconds,
                     boolean async, boolean isArrJson, Object msgObj) {
        final List<String> serverIds = serverHolder.getServerIds(serverType);
        if (Objects.isNull(serverIds) || serverIds.isEmpty()) {
            log.warn("MessageInformer send serverIds is null");
            return;
        }
        String topicName = serverType == ServerType.ROOM ? GameConstants.H2R_TOPIC_NAME : GameConstants.R2H_TOPIC_NAME;
        for (String serverId : serverIds) {
            send(serverId, topicName, msgType, delaySeconds, async, isArrJson, msgObj);
        }
    }

}
