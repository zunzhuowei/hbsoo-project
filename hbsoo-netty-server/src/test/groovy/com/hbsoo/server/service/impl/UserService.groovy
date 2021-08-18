package com.hbsoo.server.service.impl

import com.hbsoo.server.model.User
import com.hbsoo.server.service.IUserService
import org.springframework.stereotype.Service

/**
 * Created by zun.wei on 2021/8/14.
 *
 */
@Service
class UserService implements IUserService{


    @Override
    void addUser(User user) {
        println "user = $user"
    }

    @Override
    User getUser() {
        def user = new User()
        user.nickname = "wangb"
        return user
    }

    @Override
    void regUser() {
        println "true = true"
    }

    @Override
    void regUser1() {
        println "true = 2true"
    }
}
