package com.hbsoo.client;

import com.hbsoo.client.heartbeat.HeartbeatHandler;
import com.hbsoo.client.manager.ClientSessionManager;
import com.hbsoo.handler.constants.HotSwapSwitch;
import com.hbsoo.handler.processor.channel.handshaker.HBSClientHandshaker;
import com.hbsoo.handler.cfg.ClientChannelHandlerRegister;
import com.hbsoo.handler.constants.ClientProtocolType;
import com.hbsoo.handler.processor.message.ClientGlobalExceptionHandler;
import com.hbsoo.handler.processor.message.GlobalExceptionHandler;
import com.hbsoo.utils.commons.GroovySrcScanner;
import com.hbsoo.utils.hotswap.HotSwapClass;
import com.hbsoo.utils.hotswap.HotSwapHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/7/29.
 */
public class HbsooClient {


    private final Bootstrap bootstrap;
    private final EventLoopGroup group;
    private String connectHost;
    private Integer listenPort;

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
     * @return
     */
    public HbsooClient protocolType(ClientProtocolType type) {
        HbsooClient hbsooClient = this;
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch){
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new IdleStateHandler
                        (0,3,0, TimeUnit.SECONDS));
                pipeline.addLast(new HeartbeatHandler(hbsooClient));

                pipeline.addLast(new HBSClientHandshaker(
                        ClientSessionManager::add,
                        ClientSessionManager::remove
                ));
                final List<ChannelHandler> handler = ClientChannelHandlerRegister.get(type);
                if (Objects.nonNull(handler)) {
                    pipeline.addLast(handler.toArray(new ChannelHandler[0]));
                }
                pipeline.addLast(new ClientGlobalExceptionHandler(() -> tryReconnect(hbsooClient)));
            }
        });
        return this;
    }

    /**
     * 尝试重新连接
     * @param hbsooClient 客户端
     */
    private Channel tryReconnect(HbsooClient hbsooClient) {
        try {
            TimeUnit.SECONDS.sleep(3);
            return hbsooClient.reConnect();
        } catch (Exception e) {
            e.printStackTrace();
            return tryReconnect(hbsooClient);
        }
    }

    /**
     * 首次连接
     * @param connectHost 连接地址
     * @param listenPort 连接端口
     */
    public Channel connect(String connectHost, Integer listenPort) throws InterruptedException {
        this.connectHost = connectHost;
        this.listenPort = listenPort;
        Channel ch = this.bootstrap.connect(connectHost, listenPort).sync().channel();
        return ch;
    }

    /**
     * 重连
     */
    public Channel reConnect() throws InterruptedException {
        return connect(connectHost, listenPort);
    }


    public void shutdown() {
        this.group.shutdownGracefully();
    }

    /**
     * 启动热更新
     */
    public HbsooClient enableHotSwap(String... groovySrcDir) {
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
