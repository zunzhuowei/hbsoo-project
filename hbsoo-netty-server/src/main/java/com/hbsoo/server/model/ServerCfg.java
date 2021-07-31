package com.hbsoo.server.model;

import io.netty.channel.ChannelPipeline;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Data
@Accessors(chain = true)
public class ServerCfg {

    private Integer bossThreads;
    private Integer workerThreads;
    private Integer port;
    Consumer<ChannelPipeline> pipelineConsumer;

}
