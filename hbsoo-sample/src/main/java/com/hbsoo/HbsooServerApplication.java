package com.hbsoo;

import com.hbsoo.server.HbsooServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by zun.wei on 2021/7/29.
 */
@SpringBootApplication
public class HbsooServerApplication {


    public static void main(String[] args) throws InterruptedException {
        final ConfigurableApplicationContext context = SpringApplication.run(HbsooServerApplication.class, args);
        final HbsooServer hbsooServer = context.getBean(HbsooServer.class);
        hbsooServer.start();
    }



}
