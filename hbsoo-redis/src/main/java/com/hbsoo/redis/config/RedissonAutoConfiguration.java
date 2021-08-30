package com.hbsoo.redis.config;

import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : weizhuozun
 * @version V1.0.0
 * @Description:
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(RedissonProperties.class)
@ConditionalOnClass(RedissonClient.class)
public class RedissonAutoConfiguration {


    @Autowired
    private RedissonProperties redissonProperties;


    @Bean
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() {
        String hostPortsProperties = redissonProperties.getHostPorts();
        log.debug("RedissonAutoConfiguration hostPortsProperties --::{}", hostPortsProperties);
        if (StringUtil.isBlank(hostPortsProperties)) {
            throw new RuntimeException("redisson not config");
        }

        Config config = new Config();
        String[] hostPorts = hostPortsProperties.split(",");
        if (hostPorts.length == 1) {
            int connectionPoolSize = redissonProperties.getConnectionPoolSize();
            int connectionMinimumIdleSize = redissonProperties.getConnectionMinimumIdleSize();
            int db = redissonProperties.getDb();
            final String username = redissonProperties.getUsername();
            final String password = redissonProperties.getPassword();
            final SingleServerConfig singleServerConfig = config.useSingleServer().setAddress("redis://" + hostPorts[0])
                    .setDatabase(db).setConnectionPoolSize(connectionPoolSize)
                    .setConnectionMinimumIdleSize(connectionMinimumIdleSize);
            if (StringUtils.hasLength(username)) {
                singleServerConfig.setUsername(username);
            }
            if (StringUtils.hasLength(password)) {
                singleServerConfig.setPassword(password);
            }
            return Redisson.create(config);
        }

        List<String> clusterNodes = new ArrayList<>();
        for (String hostPort : hostPorts) {
            clusterNodes.add("redis://" + hostPort);
        }
        String[] ss = new String[clusterNodes.size()];
        String[] array = clusterNodes.toArray(ss);
        config.useClusterServers().addNodeAddress(array);
        return Redisson.create(config);
    }



}
