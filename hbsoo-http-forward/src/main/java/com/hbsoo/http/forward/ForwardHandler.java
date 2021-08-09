package com.hbsoo.http.forward;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * Created by zun.wei on 2021/8/9.
 */
public class ForwardHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private final String forwardHost;
    private final Integer forwardPort;

    public ForwardHandler(String forwardHost,Integer forwardPort) {
        this.forwardHost = forwardHost;
        this.forwardPort = forwardPort;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65535));
                        pipeline.addLast(new ClientHandler(ctx));
                    }
                });
        final Channel channel = bootstrap.connect(forwardHost, forwardPort).channel();
        final FullHttpRequest duplicate = msg.duplicate();
        channel.writeAndFlush(duplicate);//.addListener(ChannelFutureListener.CLOSE);
    }


}
