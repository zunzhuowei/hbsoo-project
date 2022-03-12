package com.hbsoo.server;

import com.hbsoo.handler.constants.HotSwapSwitch;
import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.processor.ProtocolSelectorHandler;
import com.hbsoo.server.manager.ServerChannelManager;
import com.hbsoo.utils.commons.GroovySrcScanner;
import com.hbsoo.utils.hotswap.HotSwapClass;
import com.hbsoo.utils.hotswap.HotSwapHolder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Slf4j
public class HbsooServer {


    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private boolean heartbeatCheck = false;
    private boolean handshakerCheck = false;
    private Consumer<ChannelHandlerContext> channelAddConsumer;
    private Consumer<ChannelHandlerContext> channelRemoveConsumer;

    /**
     * 创建服务
     * @param bossThreads boss线程数
     * @param workerThreads worker 线程数
     * @return HbsooServer
     */
    public HbsooServer create(Integer bossThreads, Integer workerThreads) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreads);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreads);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO));
        //.childHandler(new CustomChannelInitializer(pipelineConsumer));
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.bootstrap = bootstrap;
        return this;
    }

    public HbsooServer heartbeatCheck(boolean heartbeatCheck) {
        this.heartbeatCheck = heartbeatCheck;
        return this;
    }


    public HbsooServer handshakerCheck(boolean handshakerCheck) {
        this.handshakerCheck = handshakerCheck;
        return this;
    }

    public HbsooServer channelAddConsumer(Consumer<ChannelHandlerContext> channelAddConsumer) {
        this.channelAddConsumer = channelAddConsumer;
        return this;
    }

    public HbsooServer channelRemoveConsumer(Consumer<ChannelHandlerContext> channelRemoveConsumer) {
        this.channelRemoveConsumer = channelRemoveConsumer;
        return this;
    }

    /**
     * 选择协议类型
     *
     * @param types com.hbsoo.handler.constants.ServerProtocolType
     * @return HbsooServer
     */
    public HbsooServer protocolType(ServerProtocolType... types) {
        this.bootstrap.childHandler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ProtocolSelectorHandler(
                                types,
                                ServerChannelManager::add,
                                ServerChannelManager::remove,
                                heartbeatCheck,
                                handshakerCheck,
                                channelAddConsumer,
                                channelRemoveConsumer));
                    }
                }
        )
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.SO_BACKLOG, 128)
        ;
        return this;
    }

    /**
     * 启动服务
     * @param port
     * @throws InterruptedException
     */
    public void start(Integer port) throws InterruptedException {
        Channel ch = this.bootstrap.bind(port).sync().channel();
        log.info("Open your web browser and navigate to http://127.0.0.1:" + port + '/');
        try {
            ch.closeFuture().sync();
        } finally {
            shutdown();
        }
    }

    /**
     * 关闭
     */
    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    /**
     * 启动热更新
     */
    public HbsooServer enableHotSwap(String... groovySrcDir) {
        if (!HotSwapSwitch.enable) {
            HotSwapSwitch.enable = true;
            new Thread(() -> {
                for (; ; ) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        final Set<HotSwapClass> hotSwapClasses = GroovySrcScanner.listHotSwapClazz(groovySrcDir);
                        HotSwapHolder.addOrUpdateHotSwapBeans(hotSwapClasses);
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }).start();
        }
        return this;
    }

}
