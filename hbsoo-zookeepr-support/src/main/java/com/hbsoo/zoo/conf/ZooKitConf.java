package com.hbsoo.zoo.conf;

import com.hbsoo.zoo.Zookit;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by zun.wei on 2021/12/29.
 */
@Slf4j
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(ZooKitProperties.class)
@ConditionalOnProperty(name = "zookeeper.enable", havingValue = "enable")
@ConditionalOnClass(CuratorFramework.class)
public class ZooKitConf {

    @Autowired
    private ZooKitProperties zooKitProperties;

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(CuratorFramework.class)
    public CuratorFramework curatorFramework() {
        String listOfServers = zooKitProperties.getListOfServers();
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 29);
        CuratorFramework client = CuratorFrameworkFactory.newClient(listOfServers, retryPolicy);
        client.start();
        return client;
    }

    @Bean
    public Zookit zookit() {
        return new Zookit();
    }

}
