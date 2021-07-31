package com.hbsoo.server.cfg;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.hbsoo.commons.NettyServerConstants.HBSOO_SERVER_PROPERTIES_PREFIX;

/**
 * Created by zun.wei on 2021/7/29.
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = HBSOO_SERVER_PROPERTIES_PREFIX)
public class NettyServerProperties {

    private Integer port;

    private Integer bossThreads = 1;

    private Integer workerThreads = 10;


}
