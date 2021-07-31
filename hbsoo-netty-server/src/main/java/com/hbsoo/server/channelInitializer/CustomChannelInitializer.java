package com.hbsoo.server.channelInitializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.function.Consumer;

/**
 * Created by zun.wei on 2021/7/31.
 */
public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Consumer<ChannelPipeline> pipelineConsumer;


    public CustomChannelInitializer(Consumer<ChannelPipeline> pipelineConsumer) {
        this.pipelineConsumer = pipelineConsumer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipelineConsumer.accept(pipeline);
    }

}
