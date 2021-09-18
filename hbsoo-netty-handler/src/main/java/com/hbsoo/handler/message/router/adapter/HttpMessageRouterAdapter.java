package com.hbsoo.handler.message.router.adapter;

import com.alibaba.fastjson.JSON;
import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.message.router.MessageRouter;
import com.hbsoo.handler.message.router.model.HttpParam;
import com.hbsoo.handler.message.router.model.RespType;
import com.hbsoo.handler.utils.HttpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * http 消息请求处理器
 * Created by zun.wei on 2021/7/31.
 */
@Slf4j
public abstract class HttpMessageRouterAdapter implements MessageRouter<FullHttpRequest> {

    @Override
    public ServerProtocolType getProtocolType() {
        return ServerProtocolType.HTTP;
    }

    @Override
    public void handler(Channel channel, FullHttpRequest req) {
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
     *
     * @param html HTML
     * @return 返回值
     */
    protected DefaultFullHttpResponse html(String html) {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        return HttpUtils.resp(bytes, RespType.HTML, true, HttpResponseStatus.OK).get();
    }

    /**
     * json 返回值
     *
     * @param json json
     * @return 返回值
     */
    protected DefaultFullHttpResponse json(String json) {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return HttpUtils.resp(bytes, RespType.JSON, true, HttpResponseStatus.OK).get();
    }

    /**
     * 解析请求参数
     *
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     */
    private Map<String, Object> parse(Channel channel, FullHttpRequest fullReq) {
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
            String contentType = fullReq.headers().get("Content-Type").trim().toLowerCase();
            if (contentType.contains("json")) {
                final ByteBuf content = fullReq.content();
                String msg = content.toString(CharsetUtil.UTF_8);
                Map<String, Object> params = JSON.parseObject(msg, Map.class);
                parmMap.putAll(params);
            } else {
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
        }
        // 不支持其它方法
        else {
            final DefaultFullHttpResponse response = HttpUtils.resp(null, RespType.JSON, true, HttpResponseStatus.BAD_REQUEST).get();
            channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return null;
        }
        return parmMap;
    }


}
