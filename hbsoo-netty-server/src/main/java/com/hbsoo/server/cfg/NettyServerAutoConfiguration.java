package com.hbsoo.server.cfg;

import com.hbsoo.handler.processor.message.GlobalExceptionHandler;
import com.hbsoo.handler.processor.channel.CustomChannelHandler;
import com.hbsoo.server.HbsooServer;
import com.hbsoo.server.model.ServerCfg;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(NettyServerProperties.class)
public class NettyServerAutoConfiguration {

    @Autowired
    private NettyServerProperties properties;


    @Bean(name = "hbsooServer", destroyMethod = "shutdown")
    public HbsooServer hbsooServer(@Qualifier("serverHandler") Consumer<ChannelPipeline> pipeline) {
        final Integer bossThreads = properties.getBossThreads();
        final Integer workerThreads = properties.getWorkerThreads();
        final Integer port = properties.getPort();
        ServerCfg cfg = new ServerCfg();
        cfg.setBossThreads(bossThreads).setWorkerThreads(workerThreads)
                .setPort(port).setPipelineConsumer(pipeline);
        return new HbsooServer(cfg);
    }

    @Bean("serverHandler")
    public Consumer<ChannelPipeline> serverHandler(@Qualifier("messageHandler") List<CustomChannelHandler> handlers) {
        log.info("Consumer<ChannelPipeline> handlers --::{}", handlers);
        return pip -> {
            for (CustomChannelHandler handler : handlers) {
                final List<ChannelHandler> codec = handler.codec();
                if (Objects.nonNull(codec) && !codec.isEmpty()) {
                    pip.addLast(codec.toArray(new ChannelHandler[0]));
                }
                final SimpleChannelInboundHandler handler1 = handler.handler();
                if (Objects.nonNull(handler1)) {
                    pip.addLast(handler1);
                }
            }
            pip.addLast(new GlobalExceptionHandler());
        };
    }

}
