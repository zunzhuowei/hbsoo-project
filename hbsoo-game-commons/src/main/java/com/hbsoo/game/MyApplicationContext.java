package com.hbsoo.game;

import com.hbsoo.game.commons.GameSpringBeanFactory;
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


}
