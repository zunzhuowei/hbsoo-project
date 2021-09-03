package com.hbsoo.client.websocket.sender;

import com.hbsoo.client.websocket.WebSocketClient;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/7/16.
 */
public final class SendMessage {


    private final ScheduledThreadPoolExecutor executor;
    private Channel channel;
    private final WebSocketClient.Client oldClient;
    private final String devidid;

    public SendMessage(Channel channel, WebSocketClient.Client client, String devidid) {
        this.oldClient = client;
        this.channel = channel;
        this.devidid = devidid;
        this.executor = new ScheduledThreadPoolExecutor(1);
        // heartbeat
        this.executor.scheduleAtFixedRate(this::heartbeat, 3, 3, TimeUnit.SECONDS);
    }

    /**
     * 关闭管道
     */
    public void closeChannel() throws InterruptedException {
        channel.writeAndFlush(new CloseWebSocketFrame());
        channel.closeFuture().sync();
    }

    /**
     * 心跳
     */
    public void heartbeat() {
        WebSocketFrame frame = new PingWebSocketFrame(Unpooled.wrappedBuffer(new byte[]{8, 1, 8, 1}));
        channel.writeAndFlush(frame).addListener(new ChannelFutureListener(){
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                // 发送到服务器是否成功，可以判断管道是否还可用
                boolean success = channelFuture.isSuccess();
                if (!success) {
                    final EventLoop loop = channelFuture.channel().eventLoop();
                    loop.schedule(reconnectServer(), 0, TimeUnit.SECONDS);
                }
            }
        });
    }

    /**
     * 重连服务器
     */
    public Runnable reconnectServer() {
        return () -> {
            try {
                // 关闭旧链接
                closeChannel();
                // 打开新链接
                WebSocketClient.Client client = new WebSocketClient.Client();
                client.connect(devidid);
                // 创建新管道成功则关闭旧客户端
                oldClient.shutdown();
                // 关闭线程池
                executor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    /**
     * 发送文本消息
     *
     * @param msg 文本消息
     */
    public void sendTextMsg(String msg) {
        WebSocketFrame frame = new TextWebSocketFrame(msg);
        channel.writeAndFlush(frame);
    }

//    public static void main(String[] args) throws InterruptedException {
//        Random random = new Random();
//        for (; ; ) {
//            TimeUnit.SECONDS.sleep(1);
//            final int i = random.nextInt(3);
//            System.out.println("i = " + i);
//        }
//    }

    /**
     * 发送消息到服务端
     */
    public void sendMsg2Server() {
        // 登录
        sendTextMsg("{\"action\":\"LOGIN\",\"data\":{\"deviceId\":\"" + devidid + "\",\"channel\":\"adfadsf\"}}");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //sendTextMsg("{\"action\":\"JOIN_ROOM\",\"data\":{\"gameOption\":\"Lv1\",\"gameType\":\"baccarat3patti\"}}");
        sendTextMsg("{\"action\":\"JOIN_ROOM\",\"data\":{\"gameOption\":\"Lv1\",\"gameType\":\"dragonVsTiger\"}}");

        Random random = new Random();

        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                TimeUnit.SECONDS.sleep(3);
                final int i = random.nextInt(7);
                String betType = i == 1 ? "RED" : i == 2 ? "BLUE" : "DRAW";
                if (i > 2) {
                    continue;
                }
                String bet = "{\"action\":\"DRAGON_VS_TIGER_BET\",\"data\":{\"betType\":\""+betType+"\",\"score\":\"1000\"}}";
                sendTextMsg(bet);

                /*try {
                    String msg = console.readLine();
                    if (msg == null || msg.trim().equals("")) {
                        break;
                    } else if ("bye".equals(msg.toLowerCase())) {
                        closeChannel();
                        break;
                    } else if ("ping".equals(msg.toLowerCase())) {
                        heartbeat();
                    } else {
                        sendTextMsg(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            reconnectServer().run();
        }
    }

}
