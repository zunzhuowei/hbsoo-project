package com.hbsoo.server.str

import com.hbsoo.handler.message.router.adapter.HttpMessageRouterAdapter
import com.hbsoo.handler.message.router.model.HttpParam
import com.hbsoo.msg.annotation.HttpHandler
import com.hbsoo.server.model.User
import com.hbsoo.server.service.IUserService
import io.netty.handler.codec.http.DefaultFullHttpResponse
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by zun.wei on 2021/8/9.
 *
 */
@HttpHandler(["/2", "/index2"])
class HttpMsgTypeHandler2 extends HttpMessageRouterAdapter {

    @Autowired
    private IUserService userService

    @Override
    protected DefaultFullHttpResponse handler(String uri, HttpParam httpParam) {
        User user = new User()
        user.username = "zhangsan"
        user.nickname = "666"
        user.age = 18
        user.address = "10"

        userService.addUser(user)
        userService.regUser()
        userService.regUser1()

//        def user1 = userService.getUser()
//        println "user1 = $user1"

        return html("<h1>22222${user.toString()}</h1>")
    }


    /*
    1. 不能指定类型接收热更类型问题
    2. handler（如：HttpMsgTypeHandler）中 导入新类时，无效不刷新问题。

     */

}
