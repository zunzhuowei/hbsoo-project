//package com.hbsoo.game.hall.msg;
//
//import com.hbsoo.game.commons.GameConstants;
//import com.hbsoo.game.commons.ServerHolder;
//import com.hbsoo.game.commons.ServerType;
//import com.hbsoo.game.inner.InnerMessage;
//import com.hbsoo.game.inner.MessageInformer;
//import lombok.extern.slf4j.Slf4j;
//import org.redisson.api.RTopic;
//import org.redisson.api.RedissonClient;
//import org.redisson.codec.SerializationCodec;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.util.Objects;
//import java.util.Set;
//
///**
// * Created by zun.wei on 2021/8/31.
// */
//@Slf4j
//public class RoomMessageInformer {
//
//    @Autowired
//    private MessageInformer messageInformer;
//
//    @Autowired
//    private ServerHolder serverHolder;
//
////    public void send(String serverId, int msgType, Object msgObj) {
////        send(serverId, msgType, 0L, false, false, msgObj);
////    }
////
////    public void send(String serverId, int msgType, Long delaySeconds, Object msgObj) {
////        send(serverId, msgType, delaySeconds, false, false, msgObj);
////    }
//
//    public void send(String toServerId, int msgType, Long delaySeconds, boolean async, boolean isArrJson, Object msgObj) {
//        messageInformer.send(toServerId, GameConstants.H2R_TOPIC_NAME, msgType, delaySeconds, async, isArrJson, msgObj);
//    }
//
//    public void send(ServerType serverType, Long playerId,
//                     int msgType, Long delaySeconds,
//                     boolean async, boolean isArrJson, Object msgObj) {
//        final String serverId = serverHolder.getServerId(playerId, serverType);
//        if (!StringUtils.hasLength(serverId)) {
//            log.warn("RoomMessageInformer send serverId is null");
//            return;
//        }
//        send(serverId, msgType, delaySeconds, async, isArrJson, msgObj);
//    }
//
//    public void send(ServerType serverType,
//                     int msgType, Long delaySeconds,
//                     boolean async, boolean isArrJson, Object msgObj) {
//        final Set<String> serverIds = serverHolder.getServerIds(serverType);
//        if (Objects.isNull(serverIds) || serverIds.isEmpty()) {
//            log.warn("RoomMessageInformer send serverIds is null");
//            return;
//        }
//        for (String serverId : serverIds) {
//            send(serverId, msgType, delaySeconds, async, isArrJson, msgObj);
//        }
//    }
//
//}
