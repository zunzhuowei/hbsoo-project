package com.hbsoo.handler.cfg;

import com.hbsoo.handler.utils.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zun.wei on 2021/7/31.
 */
@Slf4j
@Configuration
public class HandlerAutoConfiguration {


    @Bean
    public SpringBeanFactory springBeanFactory() {
        return new SpringBeanFactory();
    }

}
