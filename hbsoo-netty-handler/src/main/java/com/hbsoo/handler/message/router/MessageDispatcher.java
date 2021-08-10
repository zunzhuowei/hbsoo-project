package com.hbsoo.handler.message.router;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.model.MessageTask;
import com.hbsoo.handler.message.router.model.RespType;
import com.hbsoo.handler.utils.HttpUtils;
import com.hbsoo.handler.utils.SpringBeanFactory;
import com.hbsoo.msg.annotation.HttpHandler;
import com.hbsoo.msg.annotation.StrHandler;
import com.hbsoo.msg.model.HBSMessage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zun.wei on 2021/8/10.
 */
@Slf4j
public final class MessageDispatcher {


    static BlockingQueue<MessageTask> queue = new DelayQueue<>();
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void dispatchMsg(ChannelHandlerContext ctx, Object msg, ServerProtocolType protocolType) {
        dispatchMsg(new MessageTask().setCtx(ctx).setProtocolType(protocolType).setMsg(msg));
    }

    public static void dispatchMsg(MessageTask messageTask) {
        executorService.execute(() -> {
            try {
                queue.put(messageTask);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executorService.execute(() -> {
            // 从队列中获取任务，并执行任务
            try {
                MessageTask task = queue.take();
                final ChannelHandlerContext ctx = task.getCtx();
                final Object msg = task.getMsg();
                final ServerProtocolType protocolType = task.getProtocolType();
                consumptionMessge(ctx, msg, protocolType);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 消费消息
     * @param ctx
     * @param msg
     * @param protocolType
     */
    private static void consumptionMessge(ChannelHandlerContext ctx, Object msg, ServerProtocolType protocolType) {
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
                            handler.handler(ctx, msg);
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
                            handler.handler(ctx, msg);
                            b = true;
                        }
                    }
                }
                if (!b) {
                    log.info("http not exist uri handler [{}]", split[0]);
                    final DefaultFullHttpResponse response =
                            HttpUtils.resp(null, RespType.HTML, true, HttpResponseStatus.NOT_FOUND).get();
                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                }
                break;
            }
        }
    }

}
