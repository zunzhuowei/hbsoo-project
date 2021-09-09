package com.hbsoo.game.commons;

import java.util.List;

/**
 * Created by zun.wei on 2021/9/9.
 *  内部消息分发
 */
public final class InnerMessageDispatcher {

    void dispatcher() {
        List<InnerMessageProcessor> processors = SpringBeanFactory.getBeansOfTypeWithAnnotation
                (InnerMessageProcessor.class, InnerProcessor.class);

    }

}
