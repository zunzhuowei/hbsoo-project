package com.hbsoo.server.service.impl

import com.hbsoo.commons.SpringBean
import com.hbsoo.server.model.User
import com.hbsoo.server.service.IUserService

/**
 * Created by zun.wei on 2021/8/14.
 *
 */
@SpringBean
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
}
