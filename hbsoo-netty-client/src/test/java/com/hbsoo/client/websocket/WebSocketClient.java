package com.hbsoo.client.websocket;

import com.hbsoo.client.websocket.handler.WebSocketClientHandler;
import com.hbsoo.client.websocket.handler.WebSocketClientHandshakeHandler;
import com.hbsoo.client.websocket.sender.SendMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zun.wei on 2021/7/15.
 */
public class WebSocketClient {


    public static void main(String[] args) throws Exception {
        AtomicInteger atomicInteger = new AtomicInteger(600);
        for (int i = 0; i < 600; i++) {
            String devidid = "devidid-" + atomicInteger.incrementAndGet();
            new Thread(() -> {
                Client client = new Client();
                try {
                    client.connect(devidid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


    public static class Client {
        final String URL = System.getProperty("url", "wss://127.0.0.1:1000/websocket");
        //final String URL = System.getProperty("url", "wss://192.168.1.146:5011/websocket");
//        final String URL = System.getProperty("url", "wss://192.168.1.100:1000/websocket");

        EventLoopGroup group = new NioEventLoopGroup(1);

        /**
         * ???????????????
         */
        public void connect(String devidid) throws Exception {
            URI uri = new URI(URL);
            String scheme = uri.getScheme() == null? "ws" : uri.getScheme();
            final String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
            final int port;
            if (uri.getPort() == -1) {
                if ("ws".equalsIgnoreCase(scheme)) {
                    port = 80;
                } else if ("wss".equalsIgnoreCase(scheme)) {
                    port = 443;
                } else {
                    port = -1;
                }
            } else {
                port = uri.getPort();
            }

            if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                System.err.println("Only WS(S) is supported.");
                return;
            }

            final boolean ssl = "wss".equalsIgnoreCase(scheme);
            final SslContext sslCtx;
            if (ssl) {
                sslCtx = SslContextBuilder.forClient()
                        .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } else {
                sslCtx = null;
            }

            try {
                // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
                // If you change it to V00, ping is not supported and remember to change
                // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
                WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                        uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
                WebSocketClientHandshakeHandler handler = new WebSocketClientHandshakeHandler(handshaker);

                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline p = ch.pipeline();
                                if (sslCtx != null) {
                                    p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                                }
                                p.addLast(new HttpClientCodec());
                                p.addLast(new HttpObjectAggregator(8192));
                                p.addLast(WebSocketClientCompressionHandler.INSTANCE);
                                p.addLast(handler);
                                p.addLast(new WebSocketClientHandler());
                                //p.addLast(new ClientProtobufHandler());
                            }
                        });

                Channel ch = b.connect(uri.getHost(), port).sync().channel();
                handler.handshakeFuture().sync();
                new SendMessage(ch, this,devidid).sendMsg2Server();
            } finally {
                //group.shutdownGracefully();
            }
        }

        public void shutdown() {
            group.shutdownGracefully();
        }
    }



}
