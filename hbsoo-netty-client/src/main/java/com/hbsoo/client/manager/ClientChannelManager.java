package com.hbsoo.client.manager;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by zun.wei on 2021/8/6.
 */
public final class ClientChannelManager {

    /**
     * 全部客户端管道保存在这里
     */
    private static final Map<String, Channel> ALL_CLIENT_CHANNEL = new ConcurrentHashMap<>();

    /**
     * 添加管道
     * @param channels 管道
     */
    public static void add(Channel... channels) {
        for (Channel channel : channels) {
            ALL_CLIENT_CHANNEL.put(channel.id().asLongText(), channel);
        }
    }

    /**
     * 移除管道
     * @param channels 管道
     */
    public static void remove(Channel... channels) {
        for (Channel channel : channels) {
            ALL_CLIENT_CHANNEL.remove(channel.id().asLongText());
        }
    }


}
