package com.hbsoo.utils.processor;

import java.util.function.Function;

/**
 * Created by zun.wei on 2021/7/24.
 */
public final class ItemProcessor<Input, Output> {

    /**
     * 处理线程类型
     */
    private ProcessorType type;

    /**
     * 多线程，线程选择系数；单线程处理时不用设置
     */
    private Integer threadRatio;

    /**
     * 多线程，线程选择系数；单线程处理时不用设置;
     *  输入是上一个处理器的结果，输出是线程选择系数
     */
    private Function<Input, Integer> threadRatioFun;

    /**
     * 处理逻辑函数
     * 输入是上一个处理器的结果，输出是下一个处理函数的输入
     */
    private Function<Input, Output> logic;

//    public ItemProcessor(Class<Input> inputClass, Class<Output> outputClass){
//
//    }

    ProcessorType getType() {
        return type;
    }

    public ItemProcessor<Input, Output> setType(ProcessorType type) {
        this.type = type;
        return this;
    }

    Function<Input, Output> getLogic() {
        return logic;
    }

    /**
     * Function 函数的输入参数为上一个处理流程的结果值；
     * 输出参数为下一个处理器的输入参数；
     * @param logic 处理器逻辑函数
     * @return  ItemProcessor
     */
    public ItemProcessor<Input, Output> setLogic(Function<Input, Output> logic) {
        this.logic = logic;
        return this;
    }

    Integer getThreadRatio() {
        return threadRatio;
    }

    public ItemProcessor<Input, Output> setThreadRatio(Integer threadRatio) {
        this.threadRatio = threadRatio;
        return this;
    }

    Function<Input, Integer> getThreadRatioFun() {
        return threadRatioFun;
    }

    /**
     * Function 函数的输入参数为上一个处理流程的结果值；
     * 输出参数为 线程选择系数
     * @param threadRatioFun 线程选择系数函数
     * @return ItemProcessor
     */
    public ItemProcessor<Input, Output> setThreadRatioFun(Function<Input, Integer> threadRatioFun) {
        this.threadRatioFun = threadRatioFun;
        return this;
    }
}
