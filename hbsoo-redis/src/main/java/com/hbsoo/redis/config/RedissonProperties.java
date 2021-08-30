package com.hbsoo.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : weizhuozun
 * @version V1.0.0
 * @Description:
 */
@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {



    private String hostPorts;

    private int db = 0;

    private int connectionPoolSize = 5;

    private int connectionMinimumIdleSize = 3;

    private String username;

    private String password;
}
