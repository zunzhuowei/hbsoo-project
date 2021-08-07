package com.hbsoo.utils.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by zun.wei on 2021/7/24.
 */
final class SingleThreadProcessor implements Processor{

    /**
     * 业务处理线程池，用单线程处理，保证线程安全
     */
    private final ExecutorService handlerThread;

    private SingleThreadProcessor() {
        handlerThread = Executors.newSingleThreadExecutor(r -> {
            final Thread thread = new Thread(r);
            thread.setName("single-thread");
            thread.setUncaughtExceptionHandler((t, e) -> {
                e.printStackTrace();
            });
            return thread;
        });
    }

    /**
     * 单例
     */
    private static final SingleThreadProcessor processor = new SingleThreadProcessor();

    /**
     * 获取单例
     * @return 单例
     */
    static SingleThreadProcessor getInstance() {
        return processor;
    }


    @Override
    public void process(Integer threadRatio, Supplier<Object> inputFun, Function<Object, Object> logicFunction, Consumer<Object> outputFun) {
        SingleThreadProcessor.getInstance().handlerThread.execute(() -> {
            final Object inputValue = inputFun.get();
            final Object outputValue = logicFunction.apply(inputValue);
            outputFun.accept(outputValue);
        });
    }
}
