package com.hbsoo.server;

import com.hbsoo.handler.processor.channel.handshaker.HBSServerHandshaker;
import com.hbsoo.handler.cfg.ServerChannelHandlerRegister;
import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.processor.channel.CustomChannelHandler;
import com.hbsoo.handler.processor.message.GlobalExceptionHandler;
import com.hbsoo.server.manager.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Slf4j
public class HbsooServer {


    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

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

                        pipeline.addLast(new HBSServerHandshaker
                                (SessionManager::add, SessionManager::remove));
                        for (ServerProtocolType type : types) {
                            final CustomChannelHandler handler = ServerChannelHandlerRegister.get(type);
                            if (Objects.nonNull(handler)) {
                                // 编解码器
                                final List<ChannelHandler> codec = handler.codec();
                                if (Objects.nonNull(codec) && !codec.isEmpty()) {
                                    pipeline.addLast(codec.toArray(new ChannelHandler[0]));
                                }
                                // 消息处理器
                                final SimpleChannelInboundHandler handler1 = handler.handler();
                                if (Objects.nonNull(handler1)) {
                                    pipeline.addLast(handler1);
                                }
                            }
                        }
                        pipeline.addLast(new GlobalExceptionHandler());
                    }
                }
        );
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


    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


}
