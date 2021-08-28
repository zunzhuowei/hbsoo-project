package com.hbsoo.handler.message.router;

import com.hbsoo.handler.constants.HotSwapSwitch;
import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.model.MessageTask;
import com.hbsoo.handler.message.router.model.RespType;
import com.hbsoo.handler.utils.HttpUtils;
import com.hbsoo.handler.utils.SpringBeanFactory;
import com.hbsoo.msg.annotation.*;
import com.hbsoo.msg.model.HBSMessage;
import com.hbsoo.utils.hotswap.HotSwapHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by zun.wei on 2021/8/10.
 */
@Slf4j
public final class MessageDispatcher {

    /**
     * 延迟队列
     */
    static BlockingQueue<MessageTask> queue = new DelayQueue<>();
    /***
     * 线程池
     */
    static ExecutorService executorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
        final AtomicInteger atomicInteger = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("dispatcher-" + atomicInteger.incrementAndGet());
            thread.setUncaughtExceptionHandler((thread1, throwable) -> {
                throwable.printStackTrace();
            });
            return thread;
        }
    });

    /**
     * 消息转发
     *
     * @param channel      消息管道
     * @param msg          消息内容
     * @param protocolType 协议类型
     */
    public static void dispatchMsg(Channel channel, Object msg, ServerProtocolType protocolType) {
        dispatchMsg(new MessageTask().setChannel(channel).setProtocolType(protocolType).setMsg(msg));
    }

    /**
     * 消息转发
     *
     * @param delaySecond  延迟时间（秒）数
     * @param channel      消息管道
     * @param msg          消息内容
     * @param protocolType 协议类型
     */
    public static void dispatchMsg(long delaySecond, Channel channel, Object msg, ServerProtocolType protocolType) {
        dispatchMsg(new MessageTask(delaySecond).setChannel(channel).setProtocolType(protocolType).setMsg(msg));
    }

    /**
     * 消息转发
     *
     * @param messageTask 消息任务
     */
    public static void dispatchMsg(MessageTask messageTask) {
        //executorService.execute(() -> {
        try {
            queue.put(messageTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //});
    }

    /**
     * 消费消息
     */
    static {
        new Thread(() -> {
            while (true) {
                try {
                    // 从队列中获取任务，并执行任务
                    MessageTask task = queue.take();
                    final Channel channel = task.getChannel();
                    final Object msg = task.getMsg();
                    final ServerProtocolType protocolType = task.getProtocolType();
                    executorService.execute(() -> consumptionMessage(channel, msg, protocolType));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 消费消息
     *
     * @param channel      消息管道
     * @param msg          消息内容
     * @param protocolType 协议类型
     */
    private static void consumptionMessage(Channel channel, Object msg, ServerProtocolType protocolType) {
        switch (protocolType) {
            case STRING: {
                final List<MessageRouter> handlers = getMessageRouter(protocolType);
                HBSMessage<String> message = (HBSMessage<String>) msg;
                final short msgType = message.getHeader().getMsgType();
                boolean b = false;
                for (MessageRouter handler : handlers) {
                    final StrHandler httpHandler = handler.getClass().getAnnotation(StrHandler.class);
                    final int[] value = httpHandler.value();
                    for (int i : value) {
                        if (i == msgType) {
                            handler.handler(channel, msg);
                            b = true;
                        }
                    }
                }
                if (!b) {
                    log.warn("msgType [{}] handler not found!", msgType);
                }
                break;
            }
            case WEBSOCKET_TEXT:{
                final List<MessageRouter> handlers = getMessageRouter(protocolType);
                HBSMessage<String> message = (HBSMessage<String>) msg;
                final short msgType = message.getHeader().getMsgType();
                boolean b = false;
                for (MessageRouter handler : handlers) {
                    final TextWebSocketHandler httpHandler = handler.getClass().getAnnotation(TextWebSocketHandler.class);
                    final int[] value = httpHandler.value();
                    for (int i : value) {
                        if (i == msgType) {
                            handler.handler(channel, msg);
                            b = true;
                        }
                    }
                }
                if (!b) {
                    log.warn("msgType [{}] handler not found!", msgType);
                }
                break;
            }
            case WEBSOCKET_BINARY:{
                final List<MessageRouter> handlers = getMessageRouter(protocolType);
                HBSMessage<ByteBuf> message = (HBSMessage<ByteBuf>) msg;
                final short msgType = message.getHeader().getMsgType();
                boolean b = false;
                for (MessageRouter handler : handlers) {
                    final BinaryWebSocketHandler httpHandler = handler.getClass().getAnnotation(BinaryWebSocketHandler.class);
                    final int[] value = httpHandler.value();
                    for (int i : value) {
                        if (i == msgType) {
                            handler.handler(channel, msg);
                            b = true;
                        }
                    }
                }
                if (!b) {
                    log.warn("msgType [{}] handler not found!", msgType);
                }
                break;
            }
            case PROTOBUF: {
                final List<MessageRouter> handlers = getMessageRouter(protocolType);
                HBSMessage<byte[]> message = (HBSMessage<byte[]>) msg;
                final short msgType = message.getHeader().getMsgType();
                boolean b = false;
                for (MessageRouter handler : handlers) {
                    final ProtobufHandler httpHandler = handler.getClass().getAnnotation(ProtobufHandler.class);
                    final int[] value = httpHandler.value();
                    for (int i : value) {
                        if (i == msgType) {
                            handler.handler(channel, msg);
                            b = true;
                        }
                    }
                }
                if (!b) {
                    log.warn("msgType [{}] handler not found!", msgType);
                }
                break;
            }
            case WEBSOCKET_PROTOBUF:{
                final List<MessageRouter> handlers = getMessageRouter(protocolType);
                HBSMessage<byte[]> message = (HBSMessage<byte[]>) msg;
                final short msgType = message.getHeader().getMsgType();
                boolean b = false;
                for (MessageRouter handler : handlers) {
                    final WebSocketProtobufHandler httpHandler = handler.getClass().getAnnotation(WebSocketProtobufHandler.class);
                    final int[] value = httpHandler.value();
                    for (int i : value) {
                        if (i == msgType) {
                            handler.handler(channel, msg);
                            b = true;
                        }
                    }
                }
                if (!b) {
                    log.warn("msgType [{}] handler not found!", msgType);
                }
                break;
            }
            case HTTP: {
                FullHttpRequest request = (FullHttpRequest) msg;
                String uri = request.uri();
                String[] split = uri.split("[?]");
                final List<MessageRouter> handlers = getMessageRouter(protocolType);
                boolean b = false;
                for (MessageRouter handler : handlers) {
                    final HttpHandler httpHandler = handler.getClass().getAnnotation(HttpHandler.class);
                    final String[] values = httpHandler.value();
                    for (String value : values) {
                        if (value.equals(split[0])) {
                            try {
                                handler.handler(channel, msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                                channel.close();
                            }
                            b = true;
                        }
                    }
                }
                if (!b) {
                    log.info("http not exist uri handler [{}]", split[0]);
                    final DefaultFullHttpResponse response =
                            HttpUtils.resp(null, RespType.HTML, true, HttpResponseStatus.NOT_FOUND).get();
                    channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                }
                break;
            }
        }
    }

    /**
     * 获取消息路由列表
     *
     * @param protocolType 消息协议类型
     * @return 路由列表
     */
    private static List<MessageRouter> getMessageRouter(ServerProtocolType protocolType) {
        final BiFunction<Class<MessageRouter>, Class<? extends Annotation>, List<MessageRouter>>
                routerFun = getRouterFun(HotSwapSwitch.enable);
        switch (protocolType) {
            case STRING:
                return routerFun.apply(MessageRouter.class, StrHandler.class);
            case PROTOBUF:
                return routerFun.apply(MessageRouter.class, ProtobufHandler.class);
            case WEBSOCKET_PROTOBUF:
                return routerFun.apply(MessageRouter.class, WebSocketProtobufHandler.class);
            case WEBSOCKET_BINARY:
                return routerFun.apply(MessageRouter.class, BinaryWebSocketHandler.class);
            case WEBSOCKET_TEXT:
                return routerFun.apply(MessageRouter.class, TextWebSocketHandler.class);
            case HTTP:
                return routerFun.apply(MessageRouter.class, HttpHandler.class);
        }
        return new ArrayList<>();
    }

    /**
     * 获取消息协议路由函数
     *
     * @param hotSwapEnable 是否使用热更
     * @return 消息协议路由函数
     */
    private static BiFunction<Class<MessageRouter>, Class<? extends Annotation>, List<MessageRouter>> getRouterFun(boolean hotSwapEnable) {
        if (hotSwapEnable) {
            return HotSwapHolder::getHotSwapBean;
        }
        return SpringBeanFactory::getBeansOfTypeWithAnnotation;
    }

}
