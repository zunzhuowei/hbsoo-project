package com.hbsoo.server.cfg;

import com.hbsoo.server.HbsooServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Configuration
@EnableConfigurationProperties(NettyServerProperties.class)
public class NettyServerAutoConfiguration {

    @Autowired
    private NettyServerProperties properties;


    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public HbsooServer hbsooServer() {
        final Integer bossThreads = properties.getBossThreads();
        final Integer workerThreads = properties.getWorkerThreads();
        final Integer port = properties.getPort();
        return new HbsooServer(bossThreads, workerThreads, port);
    }


}
