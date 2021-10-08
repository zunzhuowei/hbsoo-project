package com.hbsoo.game.inner;

import com.hbsoo.game.commons.GameSpringBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zun.wei on 2021/9/9.
 *  内部消息分发
 */
public final class InnerMessageDispatcher {

    /**
     * 处理器缓存
     */
    private static final Map<Integer, InnerMessageProcessor> cache = new ConcurrentHashMap<>();
    /**
     * 延迟队列
     */
    static BlockingQueue<InnerMessage> queue = new DelayQueue<>();

    /***
     * 线程池
     */
    private static ExecutorService executorService = Executors.newFixedThreadPool(10, new ThreadFactory() {
        final AtomicInteger atomicInteger = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("inner-dispatcher-" + atomicInteger.incrementAndGet());
            thread.setUncaughtExceptionHandler((thread1, throwable) -> {
                throwable.printStackTrace();
            });
            return thread;
        }
    });

    /**
     * 消息分发
     * @param message 内部消息
     */
    public static void dispatcher(InnerMessage message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消费消息
     */
    static {
        new Thread(() -> {
            while (true) {
                try {
                    // 从队列中获取任务，并执行任务
                    InnerMessage message = queue.take();
                    final int msgType = message.getMsgType();
                    final boolean batch = message.isBatch();
                    final String dataJson = message.getDataJson();
                    final InnerMessageProcessor innerMessageProcessor = cache.get(msgType);
                    if (Objects.nonNull(innerMessageProcessor)) {
                        executorService.execute(() -> {
                            innerMessageProcessor.process0(batch, dataJson);
                        });
                        continue;
                    }
                    List<InnerMessageProcessor> processors = GameSpringBeanFactory.getBeansOfTypeWithAnnotation
                            (InnerMessageProcessor.class, InnerProcessor.class);
                    for (InnerMessageProcessor processor : processors) {
                        final InnerProcessor innerProcessor = processor.getClass().getAnnotation(InnerProcessor.class);
                        final int value = innerProcessor.value();
                        if (value == msgType) {
                            cache.put(value, processor);
                            executorService.execute(() -> {
                                processor.process0(batch, dataJson);
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
