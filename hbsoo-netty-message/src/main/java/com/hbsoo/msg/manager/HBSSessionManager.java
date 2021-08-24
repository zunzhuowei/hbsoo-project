package com.hbsoo.msg.manager;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2021/8/20.
 */
public class HBSSessionManager {

    /**
     * 全服 session
     */
    private static final Map<SessionKey, Channel> sessions = new ConcurrentHashMap<>();

    /**
     * 添加session
     *
     * @param uid         用户id
     * @param channelType 管道类型
     * @param channel     管道
     */
    public static void add(Long uid, ChannelType channelType, Channel channel) {
        final SessionKey sessionKey = new SessionKey();
        sessionKey.setUid(uid);
        sessionKey.setChannelType(channelType);
        final String longId = channel.id().asLongText();
        sessionKey.setChannelId(longId);
        sessions.put(sessionKey, channel);
    }

    /**
     * 移除session
     *
     * @param channels 管道
     */
    public static void remove(Channel... channels) {
        Set<SessionKey> sessionKeys = new HashSet<>(sessions.keySet());
        for (Channel channel : channels) {
            final String longId = channel.id().asLongText();
            for (SessionKey sessionKey : sessionKeys) {
                final String channelId = sessionKey.getChannelId();
                if (StringUtils.equals(longId, channelId)) {
                    sessions.remove(sessionKey);
                }
            }
        }
        sessionKeys = null;
    }

    /**
     * 移除session
     *
     * @param uids 用户id
     */
    public static void remove(Long... uids) {
        Set<SessionKey> sessionKeys = new HashSet<>(sessions.keySet());
        for (Long uid : uids) {
            for (SessionKey sessionKey : sessionKeys) {
                final Long uid1 = sessionKey.getUid();
                if (Objects.equals(uid, uid1)) {
                    sessions.remove(sessionKey);
                }
            }
        }
        sessionKeys = null;
    }

    /**
     * 指定用户发消息
     *
     * @param channelType 管道类型
     * @param msg         消息
     * @param uids        用户id
     */
    public static void sendMsg(ChannelType channelType, Object msg, Long... uids) {
        for (Long uid : uids) {
            sessions.forEach((k, v) -> {
                final Long uid1 = k.getUid();
                final ChannelType channelType1 = k.getChannelType();
                if (Objects.equals(uid, uid1) && channelType == channelType1) {
                    v.writeAndFlush(msg);
                }
            });
        }
    }

    /**
     * 全服广播
     *
     * @param channelType 管道类型
     * @param msg         消息
     */
    public static void sendMsg2All(ChannelType channelType, Object msg) {
        sessions.forEach((k, v) -> {
            final ChannelType channelType1 = k.getChannelType();
            if (channelType == channelType1) {
                v.writeAndFlush(msg);
            }
        });
    }

}
