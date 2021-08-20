package com.hbsoo.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2021/8/20.
 */
public class HBSSessionManager {

    private static final  Map<SessionKey, Channel> sessions = new ConcurrentHashMap<>();

    public static void add(Long uid, ChannelType channelType, Channel channel) {
        final SessionKey sessionKey = new SessionKey();
        sessionKey.setUid(uid);
        sessionKey.setChannelType(channelType);
        final String longId = channel.id().asLongText();
        sessionKey.setChannelId(longId);
        sessions.put(sessionKey, channel);
    }


    public static void remove(Channel channel) {

    }

}
