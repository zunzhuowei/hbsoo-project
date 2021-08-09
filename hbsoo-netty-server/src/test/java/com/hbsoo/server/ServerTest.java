package com.hbsoo.server;

import com.hbsoo.handler.constants.ServerProtocolType;
import com.hbsoo.handler.utils.SpringBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by zun.wei on 2021/8/9.
 */
@SpringBootApplication
public class ServerTest {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ServerTest.class, args);
        HbsooServer server = SpringBeanFactory.getBean(HbsooServer.class);
        server.create(1,1)
                .protocolType(ServerProtocolType.HTTP,ServerProtocolType.STRING)
                .start(3333);
    }


}
