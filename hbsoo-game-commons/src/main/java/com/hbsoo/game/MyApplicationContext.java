package com.hbsoo.game;

import com.hbsoo.game.commons.GameSpringBeanFactory;
import com.hbsoo.game.commons.ServerHolder;
import com.hbsoo.game.inner.MessageInformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zun.wei on 2021/9/10.
 */
@Configuration
public class MyApplicationContext {

    @Bean
    public GameSpringBeanFactory gameSpringBeanFactory() {
        return new GameSpringBeanFactory();
    }

    @Bean
    public MessageInformer messageInformer() {
        return new MessageInformer();
    }

    @Bean
    public ServerHolder serverHolder() {
        return new ServerHolder();
    }

}
