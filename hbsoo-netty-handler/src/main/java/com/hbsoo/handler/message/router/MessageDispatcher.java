package com.hbsoo.handler.message.router;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.model.MessageTask;
import com.hbsoo.handler.message.router.model.RespType;
import com.hbsoo.handler.utils.HttpUtils;
import com.hbsoo.handler.utils.SpringBeanFactory;
import com.hbsoo.msg.annotation.HttpHandler;
import com.hbsoo.msg.annotation.StrHandler;
import com.hbsoo.msg.model.HBSMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        AtomicInteger atomicInteger = new AtomicInteger();
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("dispatcher-" + atomicInteger.incrementAndGet());
            thread.setUncaughtExceptionHandler((thread1,throwable) -> {
                throwable.printStackTrace();
            });
            return thread;
        }
    });

    /**
     * 消息转发
     * @param channel
     * @param msg
     * @param protocolType
     */
    public static void dispatchMsg(Channel channel, Object msg, ServerProtocolType protocolType) {
        dispatchMsg(new MessageTask().setChannel(channel).setProtocolType(protocolType).setMsg(msg));
    }

    /**
     * 消息转发
     * @param delaySecond 延迟时间（秒）数
     * @param channel
     * @param msg
     * @param protocolType
     */
    public static void dispatchMsg(long delaySecond, Channel channel, Object msg, ServerProtocolType protocolType) {
        dispatchMsg(new MessageTask(delaySecond).setChannel(channel).setProtocolType(protocolType).setMsg(msg));
    }

    /**
     * 消息转发
     * @param messageTask
     */
    public static void dispatchMsg(MessageTask messageTask) {
        executorService.execute(() -> {
            try {
                queue.put(messageTask);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 消费消息
     */
    static {
        executorService.execute(() -> {
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
        });
    }

    /**
     * 消费消息
     * @param channel
     * @param msg
     * @param protocolType
     */
    private static void consumptionMessage(Channel channel, Object msg, ServerProtocolType protocolType) {
        switch (protocolType) {
            case STRING:
            {
                final List<MessageRouter> handlers = SpringBeanFactory.getBeansOfTypeWithAnnotation(MessageRouter.class, StrHandler.class);
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
            case HTTP:
            {
                FullHttpRequest request = (FullHttpRequest) msg;
                String uri = request.uri();
                String[] split = uri.split("[?]");
                final List<MessageRouter> handlers = SpringBeanFactory.getBeansOfTypeWithAnnotation(MessageRouter.class, HttpHandler.class);
                boolean b = false;
                for (MessageRouter handler : handlers) {
                    final HttpHandler httpHandler = handler.getClass().getAnnotation(HttpHandler.class);
                    final String[] values = httpHandler.value();
                    for (String value : values) {
                        if (value.equals(split[0])) {
                            handler.handler(channel, msg);
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

}
