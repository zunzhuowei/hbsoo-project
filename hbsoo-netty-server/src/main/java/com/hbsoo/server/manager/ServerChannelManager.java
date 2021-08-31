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
    //private static final Map<String, Channel> ALL_CHANNEL = new ConcurrentHashMap<>();
    private static final Map<Long, Channel> USER_ALL_CHANNEL = new ConcurrentHashMap<>();

    /**
     * 添加管道
     * @param channels 管道
     */
    public static void add(Channel... channels) {
        /*for (Channel channel : channels) {
            ALL_CHANNEL.put(channel.id().asLongText(), channel);
        }*/
    }

    /**
     * 移除管道
     * @param channels 管道
     */
    public static void remove(Channel... channels) {
        /*for (Channel channel : channels) {
            ALL_CHANNEL.remove(channel.id().asLongText());
        }*/
        for (Channel channel : channels) {
            USER_ALL_CHANNEL.entrySet()
                    .removeIf(e ->
                            channel.id().asLongText()
                            .equals(e.getValue().id().asLongText())
                    );
        }
    }

    /**
     * 注册管道保存起来
     * @param userId 用户id
     * @param channel 消息管道
     */
    public static void regChannel(Long userId, Channel channel) {
        USER_ALL_CHANNEL.put(userId, channel);
    }

}
