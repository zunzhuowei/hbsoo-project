//package com.hbsoo.handler.message.router.defaults;
//
//import com.hbsoo.handler.message.router.DefaultMessageHandler;
//import com.hbsoo.handler.message.router.adapter.HttpMessageRouterAdapter;
//import com.hbsoo.handler.message.router.model.HttpParam;
//import com.hbsoo.msg.annotation.HttpHandler;
//import io.netty.handler.codec.http.DefaultFullHttpResponse;
//import io.netty.handler.codec.http.FullHttpRequest;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * Created by zun.wei on 2022/1/18.
// */
//@Slf4j
//@HttpHandler({""})
//public class DefaultHttpMessageHandler extends HttpMessageRouterAdapter implements DefaultMessageHandler<FullHttpRequest> {
//
//
//    @Override
//    protected DefaultFullHttpResponse handler(String uri, HttpParam httpParam) {
//        log.debug("DefaultHttpMessageHandler handler uri httpParam --::{},{}", uri, httpParam);
//
//        return null;
//    }
//
//    @Override
//    protected boolean autoResp() {
//        return false;
//    }
//
//}
