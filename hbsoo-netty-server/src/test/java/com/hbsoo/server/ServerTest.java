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

    /**
     * groovy 源码所在目录
     * @param relativeDir 相对目录；如：hbsoo-netty-server/src/main/groovy
     * @return E:\\java\\IdeaProjects\\hbsoo-project/hbsoo-netty-server/src/main/groovy
     */
    static String getGroovySrcDir(String relativeDir) {
        // 项目目录
        final String property = System.getProperty("user.dir");
        return property + "/" + relativeDir;
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ServerTest.class, args);
        HbsooServer server = SpringBeanFactory.getBean(HbsooServer.class);
        String dir = getGroovySrcDir("hbsoo-netty-server/src/test/groovy");
        server.create(1,1)
                .protocolType(ServerProtocolType.HTTP,ServerProtocolType.STRING)
                .enableHotSwap(dir)
                .start(3333);
    }


}
