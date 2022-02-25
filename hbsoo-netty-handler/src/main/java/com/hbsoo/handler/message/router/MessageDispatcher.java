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

    //参数初始化
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /***
     * 线程池
     */
    static ExecutorService executorService = Executors.newFixedThreadPool(CPU_COUNT * 50, new ThreadFactory() {
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
     * @param messageTask 消息任务
     */
    public static void dispatchMsg(MessageTask messageTask) {
        executorService.execute(() -> {
            final Channel channel = messageTask.getChannel();
            final Object msg = messageTask.getMsg();
            final ServerProtocolType protocolType = messageTask.getProtocolType();
            consumptionMessage(channel, msg, protocolType);
            //queue.put(messageTask);
        });
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
                //default handler
                b = defaultHandler(channel, msg, protocolType, b);
                if (!b) {
                    log.warn("protocol string msgType [{}] handler not found!,msg = {}", msgType, msg);
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
                //default handler
                b = defaultHandler(channel, msg, protocolType, b);
                if (!b) {
                    log.warn("protocol websocket text msgType [{}] handler not found!,msg = {}", msgType, msg);
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
                //default handler
                b = defaultHandler(channel, msg, protocolType, b);
                if (!b) {
                    log.warn("protocol websocket binary msgType [{}] handler not found!,msg = {}", msgType, msg);
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
                //default handler
                b = defaultHandler(channel, msg, protocolType, b);
                if (!b) {
                    log.warn("protocol protobuf msgType [{}] handler not found!,msg = {}", msgType, msg);
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
                //default handler
                b = defaultHandler(channel, msg, protocolType, b);
                if (!b) {
                    log.warn("protocol websocket protobuf msgType [{}] handler not found!,msg = {}", msgType, msg);
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
                //default handler
                b = defaultHandler(channel, msg, protocolType, b);
                if (!b) {
                    log.info("http not exist uri handler [{}],msg = {}", split[0], msg);
                    final DefaultFullHttpResponse response =
                            HttpUtils.resp(null, RespType.HTML, true, HttpResponseStatus.NOT_FOUND).get();
                    channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                }
                break;
            }
        }
    }

    /**
     * 默认处理器
     * @param channel
     * @param msg
     * @param protocolType
     * @param b
     * @return
     */
    private static boolean defaultHandler(Channel channel, Object msg, ServerProtocolType protocolType, boolean b) {
        if (!b) {
            final List<DefaultMessageHandler> defaultMessageRouter = getDefaultMessageRouter(protocolType);
            for (DefaultMessageHandler defaultMessageHandler : defaultMessageRouter) {
                try {
                    defaultMessageHandler.handler(channel, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                b = true;
            }
        }
        return b;
    }

    /**
     * 获取消息路由列表
     *
     * @param protocolType 消息协议类型
     * @return 路由列表
     */
    private static List<DefaultMessageHandler> getDefaultMessageRouter(ServerProtocolType protocolType) {
        return getRouter(protocolType, DefaultMessageHandler.class);
    }
    private static List<MessageRouter> getMessageRouter(ServerProtocolType protocolType) {
        return getRouter(protocolType, MessageRouter.class);
    }

    private static <T> List<T> getRouter(ServerProtocolType protocolType, Class<T> tClass) {
        final BiFunction<Class<T>, Class<? extends Annotation>, List<T>>
                routerFun = getRouterFun(HotSwapSwitch.enable, tClass);
        switch (protocolType) {
            case STRING:
                return routerFun.apply(tClass, StrHandler.class);
            case PROTOBUF:
                return routerFun.apply(tClass, ProtobufHandler.class);
            case WEBSOCKET_PROTOBUF:
                return routerFun.apply(tClass, WebSocketProtobufHandler.class);
            case WEBSOCKET_BINARY:
                return routerFun.apply(tClass, BinaryWebSocketHandler.class);
            case WEBSOCKET_TEXT:
                return routerFun.apply(tClass, TextWebSocketHandler.class);
            case HTTP:
                return routerFun.apply(tClass, HttpHandler.class);
        }
        return new ArrayList<>();
    }

    /**
     * 获取消息协议路由函数
     *
     * @param hotSwapEnable 是否使用热更
     * @return 消息协议路由函数
     */
    private static <T> BiFunction<Class<T>, Class<? extends Annotation>, List<T>>
    getRouterFun(boolean hotSwapEnable, Class<T> tClass) {
        if (hotSwapEnable) {
            return HotSwapHolder::getHotSwapBean;
        }
        return SpringBeanFactory::getBeansOfTypeWithAnnotation;
    }

}
