package com.hbsoo.utils.processor;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by zun.wei on 2021/7/24.
 */
final class MultiThreadProcessor implements Processor {

    /**
     * cpu核心数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * 异步线程数
     */
    public static final int asyncThreads = CPU_COUNT * 2;

    private static final Random random = new Random();

    /**
     * 异步线程
     */
    private final ExecutorService[] async;
    /**
     * 单例
     */
    static final MultiThreadProcessor instance = new MultiThreadProcessor();


    /**
     * 获取单例
     */
    static MultiThreadProcessor getInstance() {
        return instance;
    }

    /**
     * 获取异步线程池
     */
    ExecutorService[] getAsync() {
        return this.async;
    }

    /**
     * 构造器
     */
    private MultiThreadProcessor() {
        async = new ExecutorService[asyncThreads];
        AtomicInteger integer = new AtomicInteger();
        for (int i = 0; i < async.length; i++) {
            final int i1 = integer.incrementAndGet();
            async[i] = Executors.newSingleThreadExecutor(r -> {
                final Thread thread = new Thread(r);
                thread.setName("multi-thread-" + i1);
                thread.setUncaughtExceptionHandler((t, e) -> {
                    e.printStackTrace();
                });
                return thread;
            });
        }
    }


    @Override
    public void process(Integer threadRatio, Supplier<Object> inputFun, Function<Object, Object> logicFunction, Consumer<Object> outputFun) {
        if (Objects.isNull(threadRatio)) {
            threadRatio = random.nextInt(asyncThreads);
        }
        final int abs = Math.abs(threadRatio);
        final int i = abs % MultiThreadProcessor.asyncThreads;
        MultiThreadProcessor.getInstance().getAsync()[i].execute(() -> {
            final Object inputValue = inputFun.get();
            final Object outputValue = logicFunction.apply(inputValue);
            outputFun.accept(outputValue);
        });
    }

}
