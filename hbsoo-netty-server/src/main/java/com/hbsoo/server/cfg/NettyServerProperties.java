package com.hbsoo.server.cfg;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "hbsoo.netty.server")
public class NettyServerProperties {

    private Integer port;

    private Integer bossThreads = 1;

    private Integer workerThreads = 10;


}
