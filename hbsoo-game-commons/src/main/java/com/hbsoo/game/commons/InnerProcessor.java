package com.hbsoo.game.commons;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zun.wei on 2021/9/9.
 * 内部逻辑处理器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface InnerProcessor {

    /**
     * 处理的消息类型
     */
    int value();

}
