package com.hbsoo.groovy

import com.hbsoo.handler.message.router.HttpAbstractHandler
import com.hbsoo.handler.message.router.model.HttpParam
import com.hbsoo.msg.annotation.HttpHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse

/**
 * Created by zun.wei on 2021/7/31.
 *
 */
@HttpHandler(value = ["", "/", "/index"])
class TestHttpRouter extends HttpAbstractHandler {


    @Override
    protected DefaultFullHttpResponse handler(String uri, HttpParam param) {
        println param
        json("""{"result":"ok","success": true}""")
    }

}
