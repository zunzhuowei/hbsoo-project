package com.hbsoo.server.service

import com.hbsoo.server.model.User

/**
 * Created by zun.wei on 2021/8/14.
 *
 */
interface IUserService {


    void addUser(User user)


    User getUser()


}