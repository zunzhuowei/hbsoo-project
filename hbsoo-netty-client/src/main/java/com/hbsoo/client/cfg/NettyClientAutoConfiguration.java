package com.hbsoo.client.cfg;

import com.hbsoo.client.HbsooClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Slf4j
@Configuration
public class NettyClientAutoConfiguration {


    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean(name = "hbsooClient")
    public HbsooClient hbsooClient() {
        return new HbsooClient();
    }


}
