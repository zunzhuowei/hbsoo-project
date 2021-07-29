package com.hbsoo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Slf4j
public class HbsooServer {


    private final ServerBootstrap bootstrap;
    private final Integer port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;


    public HbsooServer(Integer bossThreads, Integer workerThreads, Integer port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreads);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreads);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO));
                //.childHandler(new WebSocketServerInitializer("/websocket"));

        this.port = port;
        this.bootstrap = bootstrap;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    public HbsooServer childHandler(ChannelHandler... channelHandlers) {
        for (ChannelHandler channelHandler : channelHandlers) {
            bootstrap.childHandler(channelHandler);
        }
        return this;
    }

    public void start() throws InterruptedException {
        Channel ch = bootstrap.bind(port).sync().channel();
        log.info("Open your web browser and navigate to http://127.0.0.1:" + port + '/');
        ch.closeFuture().sync();
    }


    public void init() {
        log.info("init start server!");
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


}
