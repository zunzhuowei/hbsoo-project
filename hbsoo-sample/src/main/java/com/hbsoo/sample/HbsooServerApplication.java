package com.hbsoo.sample;

import com.hbsoo.server.HbsooServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * Created by zun.wei on 2021/7/29.
 */
@SpringBootApplication
public class HbsooServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(HbsooServerApplication.class, args);
    }


    @Autowired
    private HbsooServer hbsooServer;


    @PostConstruct
    public void test() throws InterruptedException {
        hbsooServer.childHandler(new WebSocketServerInitializer("/websocket")).start();
    }

}
