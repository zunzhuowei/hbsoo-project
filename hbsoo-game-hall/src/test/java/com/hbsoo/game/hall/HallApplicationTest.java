package com.hbsoo.game.hall;

import com.alibaba.fastjson.JSON;
import com.hbsoo.game.commons.TestUser;
import com.hbsoo.game.hall.msg.RoomMessageInformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zun.wei on 2021/9/8.
 */
@SpringBootApplication
public class HallApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(HallApplicationTest.class, args);
    }


    @Autowired
    private RoomMessageInformer roomMessageInformer;


    @PostConstruct
    public void test() {
        AtomicInteger integer = new AtomicInteger();
        new Thread(() -> {
            for (; ; ) {
                TestUser testUser = new TestUser();
                testUser.setId((long) integer.incrementAndGet());
                testUser.setPhone("123214");
                testUser.setNickName("zhang san");
                roomMessageInformer.send("room_01",1, 0L, false, false, testUser);
            }
        }).start();
    }

}
