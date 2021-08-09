package com.hbsoo.server.str

import com.hbsoo.handler.message.router.adapter.HttpMessageRouterAdapter
import com.hbsoo.handler.message.router.model.HttpParam
import com.hbsoo.msg.annotation.HttpHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse

/**
 * Created by zun.wei on 2021/8/9.
 *
 */
@HttpHandler(["/", "/index", "/index.html"])
class HttpMsgTypeHandler extends HttpMessageRouterAdapter {


    @Override
    protected DefaultFullHttpResponse handler(String uri, HttpParam httpParam) {
        return html("<h1>11111</h1>")
    }


}
