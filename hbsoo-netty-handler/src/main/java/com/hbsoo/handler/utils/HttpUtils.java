package com.hbsoo.handler.utils;

import com.hbsoo.handler.message.router.model.RespType;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Objects;
import java.util.function.Supplier;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS;

/**
 * Created by zun.wei on 2021/8/8.
 */
public final class HttpUtils {


    /**
     *  http 返回值
     * @param respContent 返回值内容
     * @param type 返回类型
     * @param enableCORS 是否允许跨域
     * @param status 响应装填
     */
    public static Supplier<DefaultFullHttpResponse> resp(byte[] respContent, RespType type,
                                                   boolean enableCORS, HttpResponseStatus status) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse
                (HttpVersion.HTTP_1_1, status);
        return () -> {
            /*String host = msg.headers().get("Host");
            logger.info("host:"+host);*/

            if (Objects.nonNull(respContent)) {
                response.content().writeBytes(respContent);
            }
            String contentType = type == RespType.JSON ? "application/json;charset=utf-8" :
                    "text/html;charset=utf-8";
            response.headers().add(CONTENT_TYPE, contentType);
            response.headers().add(CONTENT_LENGTH, response.content().readableBytes());

            //允许跨域访问
            if (enableCORS) {
                response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                response.headers().set(ACCESS_CONTROL_ALLOW_HEADERS, "*");//允许headers自定义
                response.headers().set(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT,DELETE");
                response.headers().set(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }

            return response;
        };
    }


}
