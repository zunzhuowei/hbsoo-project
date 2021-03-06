package com.hbsoo.server.cfg;

import com.hbsoo.server.HbsooServer;
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
public class NettyServerAutoConfiguration {


    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean(name = "hbsooServer")
    public HbsooServer hbsooServer() {
        return new HbsooServer();
    }


}
