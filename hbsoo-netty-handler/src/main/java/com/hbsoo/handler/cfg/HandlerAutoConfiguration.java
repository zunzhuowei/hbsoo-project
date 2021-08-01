package com.hbsoo.handler.cfg;

import com.hbsoo.handler.channel.CustomChannelHandler;
import com.hbsoo.handler.constants.HandlerType;
import com.hbsoo.handler.channel.HttpChannelHandler;
import com.hbsoo.handler.channel.StringChannelHandler;
import com.hbsoo.handler.utils.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zun.wei on 2021/7/31.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(HandlerProperties.class)
public class HandlerAutoConfiguration {

    @Autowired
    private HandlerProperties handlerProperties;

    @Bean(name = "messageHandler")
    public List<CustomChannelHandler> messageHandler() {
        final Set<HandlerType> handlerTypes = handlerProperties.getHandlerTypes();
        log.info("HandlerAutoConfiguration handlerTypes --::{}", handlerTypes);
        List<CustomChannelHandler> customChannelHandlers = new ArrayList<>();
        for (HandlerType type : handlerTypes) {
            if (type == HandlerType.HTTP) {
                customChannelHandlers.add(new HttpChannelHandler());
            }
            if (type == HandlerType.STRING) {
                customChannelHandlers.add(new StringChannelHandler());
            }
        }
        return customChannelHandlers;
    }

    @Bean
    public SpringBeanFactory springBeanFactory() {
        return new SpringBeanFactory();
    }

}
