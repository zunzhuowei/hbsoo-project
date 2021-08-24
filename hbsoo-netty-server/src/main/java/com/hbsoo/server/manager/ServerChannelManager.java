package com.hbsoo.server.manager;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2021/8/4.
 */
public final class ServerChannelManager {

    /**
     * 全服消息管道
     */
    private static final Map<String, Channel> ALL_CHANNEL = new ConcurrentHashMap<>();

    /**
     * 添加管道
     * @param channels 管道
     */
    public static void add(Channel... channels) {
        for (Channel channel : channels) {
            ALL_CHANNEL.put(channel.id().asLongText(), channel);
        }
    }

    /**
     * 移除管道
     * @param channels 管道
     */
    public static void remove(Channel... channels) {
        for (Channel channel : channels) {
            ALL_CHANNEL.remove(channel.id().asLongText());
        }
    }

}
