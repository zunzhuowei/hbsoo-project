package com.hbsoo.handler.message.router;

import com.hbsoo.handler.message.router.model.HttpParam;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *  http 消息请求处理器
 * Created by zun.wei on 2021/7/31.
 */
@Slf4j
public abstract class HttpAbstractHandler implements MessageRouter<FullHttpRequest>{

    @Override
    public void handler(ChannelHandlerContext ctx, FullHttpRequest req) {
        Channel channel = ctx.channel();
        final Map<String, Object> parse = parse(channel, req);
        if (Objects.isNull(parse)) {
            return;
        }
        String uri = req.uri();
        String[] split = uri.split("[?]");
        HttpParam httpParam = new HttpParam();
        httpParam.setParamsMap(parse);
        final DefaultFullHttpResponse resp = handler(split[0], httpParam);
        log.debug("http message response --::{}", resp);
        channel.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 处理 http 消息
     *
     * @param httpParam http 请求参数消息
     * @return 返回值
     */
    protected abstract DefaultFullHttpResponse handler(String uri, HttpParam httpParam);

    /**
     * html 返回值
     * @param html HTML
     * @return 返回值
     */
    protected DefaultFullHttpResponse html(String html) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        response.content().writeBytes(bytes);
        response.headers().add("Content-Type","text/html;charset=utf-8");
        response.headers().add("Content-Length",  response.content().readableBytes());
        return response;
    }

    /**
     * json 返回值
     * @param json json
     * @return 返回值
     */
    protected DefaultFullHttpResponse json(String json) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        response.content().writeBytes(bytes);
        response.headers().add("Content-Type","application/json;charset=utf-8");
        response.headers().add("Content-Length",  response.content().readableBytes());
        return response;
    }

    /**
     * 解析请求参数
     *
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     */
    private Map<String, Object> parse(Channel channel, FullHttpRequest fullReq)  {
        HttpMethod method = fullReq.method();
        Map<String, Object> parmMap = new HashMap<>();

        // 是GET请求
        if (HttpMethod.GET == method) {
            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.uri());
            decoder.parameters().entrySet().forEach(entry -> {
                parmMap.put(
                        entry.getKey(),
                        Objects.nonNull(entry.getValue()) && entry.getValue().size() > 1 ?
                                entry.getValue() : entry.getValue().get(0)
                );
            });
        }
        // 是POST请求
        else if (HttpMethod.POST == method) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);
            decoder.offer(fullReq);
            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
            for (InterfaceHttpData parm : parmList) {
                Attribute data = (Attribute) parm;
                try {
                    parmMap.put(data.getName(), data.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 不支持其它方法
        else {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            response.headers().add("Content-Type","text/html;charset=utf-8");
            response.headers().add("Content-Length",  response.content().readableBytes());
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return null;
        }
        return parmMap;
    }


}
