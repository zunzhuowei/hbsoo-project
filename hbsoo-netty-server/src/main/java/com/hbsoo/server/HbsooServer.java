package com.hbsoo.server;

import com.hbsoo.handler.cfg.ClientChannelHandlerRegister;
import com.hbsoo.handler.cfg.ServerChannelHandlerRegister;
import com.hbsoo.handler.constants.ClientProtocolType;
import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.processor.channel.CustomChannelHandler;
import com.hbsoo.handler.processor.message.GlobalExceptionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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
     * @param types
     * @return
     */
    public HbsooServer protocolType(ServerProtocolType... types) {
        this.bootstrap.childHandler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
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

    public void start(Integer port) throws InterruptedException {
        Channel ch = this.bootstrap.bind(port).sync().channel();
        log.info("Open your web browser and navigate to http://127.0.0.1:" + port + '/');
        ch.closeFuture().sync();
    }


    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


}
