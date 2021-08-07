package com.hbsoo.utils.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Created by zun.wei on 2021/7/24.
 *  线程处理器操作者，根据提供的处理流程函数，使用多/单线程执行业务
 */
public final class ProcessorHolder {


    private final List<ItemProcessor> itemProcessors = new ArrayList<>();

    private ProcessorHolder(){}


    public static ProcessorHolder getHolder() {
        return new ProcessorHolder();
    }

    /**
     * 添加子处理流程
     * @param itemProcessor 子处理流程
     * @return ProcessorHolder
     */
    public ProcessorHolder addLast(ItemProcessor itemProcessor) {
        itemProcessors.add(itemProcessor);
        return this;
    }

    /**
     * 添加子处理流程
     * @param itemProcessor 子处理流程
     * @return ProcessorHolder
     */
    public ProcessorHolder addLast(ItemProcessor... itemProcessor) {
        itemProcessors.addAll(Arrays.asList(itemProcessor));
        return this;
    }

    /**
     * 执行处理流程
     */
    public void execute() {
        if (itemProcessors.isEmpty()) {
            return;
        }

        InputValue inputValue = new InputValue();
        AtomicInteger atomic = new AtomicInteger();
        execute(inputValue, atomic);
    }

    /**
     * 处理单个流程
     * @param inputValue 处理器输入值
     * @param atomic 计数器
     */
    private void execute(InputValue inputValue, AtomicInteger atomic) {
        final int size = itemProcessors.size();
        final int index = atomic.get();
        if (index >= size) {
            return;
        }
        Processor processor;
        final ItemProcessor itemProcessor = itemProcessors.get(index);
        final ProcessorType type = itemProcessor.getType();
        final Function<Object, Object> logic = itemProcessor.getLogic();
        final Integer threadRatio = itemProcessor.getThreadRatio();
        final Function<Object, Integer> threadRatioFun = itemProcessor.getThreadRatioFun();

        if (type == ProcessorType.MULTI) {
            processor = MultiThreadProcessor.getInstance();
        } else {
            processor = SingleThreadProcessor.getInstance();
        }

        if (Objects.nonNull(threadRatioFun)) {
            final Integer threadRatio2 = threadRatioFun.apply(inputValue.getInputObj());
            processor.process(threadRatio2, inputValue::getInputObj, logic, obj -> {
                inputValue.setInputObj(obj);
                if (atomic.incrementAndGet() < size) {
                    execute(inputValue, atomic);
                }
            });
        } else {
            processor.process(threadRatio, inputValue::getInputObj, logic, obj -> {
                inputValue.setInputObj(obj);
                if (atomic.incrementAndGet() < size) {
                    execute(inputValue, atomic);
                }
            });
        }
    }


}
