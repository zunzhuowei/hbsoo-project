//package com.hbsoo.client.websocket.handler;
//
//import com.hbsoo.protobuf.protocol.WebSocketMessage;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * Created by zun.wei on 2021/7/15.
// */
//@Slf4j
//public class ClientProtobufHandler extends SimpleChannelInboundHandler<WebSocketMessage> {
//
//
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
//        log.info(cause.getMessage());
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, WebSocketMessage msg) throws Exception {
//        //Parser<? extends GeneratedMessageV3> parserForType = msg.getParserForType();
//        System.out.println("msg = " + msg);
//    }
//
//
//}
