package com.hbsoo.utils.processor;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by zun.wei on 2021/7/24.
 */
interface Processor {


    /**
     *  处理逻辑
     * @param threadRatio 线程系数
     * @param inputFun 处理逻辑的输入值函数
     * @param logicFunction 逻辑处理器函数
     * @param outputFun 逻辑处理之后结果返回输出函数
     */
    void process(Integer threadRatio,
                 Supplier<Object> inputFun,
                 Function<Object, Object> logicFunction,
                 Consumer<Object> outputFun);



}
