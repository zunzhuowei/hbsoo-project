package com.hbsoo.game.inner;

import com.hbsoo.game.commons.GameSpringBeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2021/9/9.
 *  内部消息分发
 */
public final class InnerMessageDispatcher {

    private static final Map<Integer, InnerMessageProcessor> cache = new ConcurrentHashMap<>();

    /**
     * 消息分发
     * @param message 内部消息
     */
    public static void dispatcher(InnerMessage message) {
        final int msgType = message.getMsgType();
        final boolean batch = message.isBatch();
        final String dataJson = message.getDataJson();
        final InnerMessageProcessor innerMessageProcessor = cache.get(msgType);
        if (Objects.nonNull(innerMessageProcessor)) {
            innerMessageProcessor.process0(batch, dataJson);
            return;
        }
        List<InnerMessageProcessor> processors = GameSpringBeanFactory.getBeansOfTypeWithAnnotation
                (InnerMessageProcessor.class, InnerProcessor.class);
        for (InnerMessageProcessor processor : processors) {
            final InnerProcessor innerProcessor = processor.getClass().getAnnotation(InnerProcessor.class);
            final int value = innerProcessor.value();
            if (value == msgType) {
                cache.put(value, processor);
                processor.process0(batch, dataJson);
            }
        }

    }

}
