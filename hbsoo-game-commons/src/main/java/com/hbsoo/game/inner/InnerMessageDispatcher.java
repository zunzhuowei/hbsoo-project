package com.hbsoo.game.inner;

import com.hbsoo.game.commons.GameSpringBeanFactory;

import java.util.List;

/**
 * Created by zun.wei on 2021/9/9.
 *  内部消息分发
 */
public final class InnerMessageDispatcher {

    public static void dispatcher(InnerMessage message) {
        List<InnerMessageProcessor> processors = GameSpringBeanFactory.getBeansOfTypeWithAnnotation
                (InnerMessageProcessor.class, InnerProcessor.class);
        final int msgType = message.getMsgType();
        final boolean batch = message.isBatch();
        final String dataJson = message.getDataJson();
        for (InnerMessageProcessor processor : processors) {
            final InnerProcessor innerProcessor = processor.getClass().getAnnotation(InnerProcessor.class);
            final int value = innerProcessor.value();
            if (value == msgType) {
                processor.process0(batch, dataJson);
            }
        }

    }

}
