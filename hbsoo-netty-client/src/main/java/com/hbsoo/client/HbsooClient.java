package com.hbsoo.client;

import com.hbsoo.client.manager.ClientSessionManager;
import com.hbsoo.handler.processor.channel.handshaker.HBSClientHandshaker;
import com.hbsoo.handler.cfg.ClientChannelHandlerRegister;
import com.hbsoo.handler.constants.ClientProtocolType;
import com.hbsoo.handler.processor.channel.CustomChannelHandler;
import com.hbsoo.handler.processor.message.GlobalExceptionHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;
import java.util.Objects;

/**
 * Created by zun.wei on 2021/7/29.
 */
public class HbsooClient {


    private final Bootstrap bootstrap;
    private final EventLoopGroup group;

    public HbsooClient() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
        this.group = group;
        this.bootstrap = bootstrap;
    }

    /**
     * 选择协议类型
     * @param types
     * @return
     */
    public HbsooClient protocolType(ClientProtocolType... types) {
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch){
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HBSClientHandshaker(
                        ClientSessionManager::add,
                        ClientSessionManager::remove
                ));
                for (ClientProtocolType type : types) {
                    final CustomChannelHandler handler = ClientChannelHandlerRegister.get(type);
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
        });
        return this;
    }


    public Channel connect(String connectHost, Integer listenPort) throws InterruptedException {
        Channel ch = this.bootstrap.connect(connectHost, listenPort).sync().channel();
        return ch;
    }


    public void shutdown() {
        this.group.shutdownGracefully();
    }

}
