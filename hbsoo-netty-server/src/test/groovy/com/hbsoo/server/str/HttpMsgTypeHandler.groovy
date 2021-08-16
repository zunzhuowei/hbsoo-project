package com.hbsoo.server.str

import com.hbsoo.handler.message.router.adapter.HttpMessageRouterAdapter
import com.hbsoo.handler.message.router.model.HttpParam
import com.hbsoo.msg.annotation.HttpHandler
import com.hbsoo.server.model.User
import com.hbsoo.server.service.IUserService
import com.hbsoo.utils.hotswap.HotSwapHolder
import io.netty.handler.codec.http.DefaultFullHttpResponse

/**
 * Created by zun.wei on 2021/8/9.
 *
 */
@HttpHandler(["/", "/index", "/index.html"])
class HttpMsgTypeHandler extends HttpMessageRouterAdapter {


    @Override
    protected DefaultFullHttpResponse handler(String uri, HttpParam httpParam) {
        //User user = new User()
        //User user = HotSwapHolder.newEntity(User.class)
        def user = HotSwapHolder.newEntity(User.class)
        user.username = "zhangsan"
        user.nickname = "666"
        user.age = 18
        //user.address = 10

        def userService = HotSwapHolder.getHotSwapBean(IUserService.class)
        userService.addUser(user)

//        def user1 = userService.getUser()
//        println "user1 = $user1"

        return html("<h1>11111${user.toString()}</h1>")
    }


}
