package com.hbsoo.client.model;

import io.netty.channel.ChannelPipeline;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * Created by zun.wei on 2021/8/1.
 */
@Data
@Accessors(chain = true)
public class ClientCfg {

    private String connectHost;
    private Integer connectPort;
    Consumer<ChannelPipeline> pipelineConsumer;

}
