package com.hbsoo.client.manager;

import com.hbsoo.msg.model.HBSMessage;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.hbsoo.handler.constants.Constants.ADD_SESSION_TIME_KEY;
import static com.hbsoo.handler.constants.Constants.HANDSHAKE_KEY;

/**
 * Created by zun.wei on 2021/8/6.
 */
public final class ClientSessionManager {

    /**
     * 全部客户端管道保存在这里
     */
    private static final ChannelGroup ALL_CLIENT_CHANNEL = new DefaultChannelGroup(new DefaultEventExecutor());
    // 调度器
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    static {
        // 每十秒钟扫描一下，超过5秒钟还未握手的链接直接关闭。
        executor.scheduleAtFixedRate(() -> {
            final long now = System.currentTimeMillis();
            ALL_CLIENT_CHANNEL.parallelStream()
                    // 过滤出没有握手的管道
                    .filter(e -> Objects.isNull(e.attr(HANDSHAKE_KEY).get())
                            || !e.attr(HANDSHAKE_KEY).get())
                    // 剔除超时没有完成握手的管道
                    .forEach(e -> {
                        final Long addTime = e.attr(ADD_SESSION_TIME_KEY).get();
                        if (Objects.isNull(addTime)) {
                            e.close();
                        }
                        if (now - addTime > 5 * 1000L) {
                            e.close();
                        }
                    });

            final Iterator<Channel> iterator = ALL_CLIENT_CHANNEL.iterator();
            while (iterator.hasNext()) {
                final Channel next = iterator.next();
                boolean isOpen = next.isOpen();
                if (!isOpen) {
                    iterator.remove();
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 添加管道
     * @param channels 管道
     */
    public static void add(Channel... channels) {
        final List<Channel> channels1 = Arrays.asList(channels);
        for (Channel channel : channels1) {
            channel.attr(ADD_SESSION_TIME_KEY).set(System.currentTimeMillis());
        }
        ALL_CLIENT_CHANNEL.addAll(channels1);
    }

    /**
     * 移除管道
     * @param channels 管道
     */
    public static void remove(Channel... channels) {
        ALL_CLIENT_CHANNEL.removeAll(Arrays.asList(channels));
    }

    /**
     *  向全部客户端连接的服务器发送消息
     * @param message 消息
     */
    public static <T extends HBSMessage> void sendMsg2AllServer(T message) {
        ALL_CLIENT_CHANNEL.writeAndFlush(message);
    }

    /**
     * 用指定客户端发送消息给服务端
     * @param message 消息
     * @param channels 发送的管道
     */
    public static <T extends HBSMessage> void sendMsg2Server(T message, Channel... channels) {
        for (Channel channel : channels) {
            Channel ch = ALL_CLIENT_CHANNEL.find(channel.id());
            if (Objects.nonNull(ch)) {
                ch.writeAndFlush(message);
            }
        }
    }


}
