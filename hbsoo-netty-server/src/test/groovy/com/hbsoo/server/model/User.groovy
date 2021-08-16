package com.hbsoo.server.model


/**
 * Created by zun.wei on 2021/8/14.
 *
 */
class User {

    String username

    String nickname

    byte age

    String address

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", age=" + age +
                ", address=" + address +
                '}';
    }
}
