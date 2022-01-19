package com.hbsoo.utils.processor;

import java.util.function.Consumer;

/**
 * Created by zun.wei on 2021/8/7.
 */
public class ProcessorHolderTest {


    public static void main(String[] args) {
        ProcessorHolder.setAsyncThreads(100);
        ProcessorHolder.getHolder()
                .addLast(new ItemProcessor<Object, String>()
                        .setType(ProcessorType.MULTI)
                        .setThreadRatioFun(e -> {
                            return 1;
                        })
                        .setLogic(e -> {

                            return "first processor";
                        })
                )
                .addLast(
                        new ItemProcessor<String, Long>()
                                .setType(ProcessorType.MULTI)
                                .setLogic(e -> {
                                    System.out.println("second processor get from first processor value e = " + e);
                                    return Long.MAX_VALUE;
                                })
                )
                .addLast(
                        new ItemProcessor<Long, String>()
                                .setType(ProcessorType.SINGLE)
                                .setLogic(e -> {
                                    System.out.println("third processor get from second processor value e = " + e);
                                    return "third processor";
                                })
                )
                .addLast(
                        new ItemProcessor<String, Consumer<Long>>()
                                .setType(ProcessorType.MULTI)
                                .setLogic(e -> {
                                    System.out.println("third processor get from second processor value e = " + e);

                                    return l -> {
                                        System.out.println("l = " + l);
                                    };
                                })
                )
                .execute();
    }

}
