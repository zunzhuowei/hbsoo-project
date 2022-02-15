package com.hbsoo.server.manager;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2021/8/4.
 */
public final class ServerChannelManager {

    /**
     * 全服消息管道
     */
    private static final Map<String, Channel> ALL_CHANNEL = new ConcurrentHashMap<>();
    private static final Map<Long, Channel> USER_ALL_CHANNEL = new ConcurrentHashMap<>();

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
            USER_ALL_CHANNEL.entrySet()
                    .removeIf(e ->
                            channel.id().asLongText()
                                    .equals(e.getValue().id().asLongText())
                    );
            channel.close();
        }
    }

    public static Channel getChannelByChannelId(String channelId) {
        return ALL_CHANNEL.get(channelId);
    }

    /**
     * 注册管道保存起来
     * @param userId 用户id
     * @param channel 消息管道
     */
    public static Channel regChannel(Long userId, Channel channel) {
        USER_ALL_CHANNEL.put(userId, channel);
        return channel;
    }

    /**
     * 获取管道
     * @param userId 用户id
     * @return 消息管道
     */
    public static Channel getChannel(Long userId) {
        return USER_ALL_CHANNEL.get(userId);
    }

    /**
     * 发送消息给用户
     * @param userId 用户id
     * @param msg 消息对象
     */
    public static void sendMsg(Long userId, Object msg) {
        final Channel channel = getChannel(userId);
        if (Objects.nonNull(channel)) {
            channel.writeAndFlush(msg);
        }
    }

    public static void sendMsg2All(Object msg) {
        USER_ALL_CHANNEL.values().forEach(channel -> channel.writeAndFlush(msg));
    }

    /**
     * 发送消息给用户并且关闭管道
     * @param userId 用户id
     * @param msg 消息对象
     */
    public static void sendMsgAndClose(Long userId, Object msg) {
        final Channel channel = getChannel(userId);
        if (Objects.nonNull(channel)) {
            channel.writeAndFlush(msg);
            channel.close();
        }
    }

}
