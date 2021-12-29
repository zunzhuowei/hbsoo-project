package qs.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zun.wei on 2019/5/13 17:09.
 * Description:
 */
@Slf4j
@Configuration
public class ZookitBeanConfig {

    @Resource
    private ZookeeperConfig zookeeperConfig;

    // 命名空间
    private static final String WORKSPACE = "workspace";

    @Data
    @Component
    @ConfigurationProperties(prefix = "system.zookit")
    public class ZookeeperConfig {
        private String[] hostPorts;
    }

    @Bean("curatorFramework")
    public CuratorFramework initCuratorFramework() {
        StringBuilder servers = new StringBuilder();
        for (String hostPort : zookeeperConfig.hostPorts) {
            servers.append(hostPort).append(",");
        }
        servers.substring(0, servers.length() - 1);

        /*
         * 同步创建zk示例，原生api是异步的
         * 这一步是设置重连策略
         *
         * ExponentialBackoffRetry构造器参数：
         *  curator链接zookeeper的策略:ExponentialBackoffRetry
         *  baseSleepTimeMs：初始sleep的时间
         *  maxRetries：最大重试次数
         *  maxSleepMs：最大重试时间
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);

        // Curator客户端
        // 实例化Curator客户端，Curator的编程风格可以让我们使用方法链的形式完成客户端的实例化
        CuratorFramework client = CuratorFrameworkFactory
                .builder() // 使用工厂类来建造客户端的实例对象
                .connectString(servers.toString())  // 放入zookeeper服务器ip
                .sessionTimeoutMs(60000) //session 超时时间
                .connectionTimeoutMs(1500) //连接超时时间
                .retryPolicy(retryPolicy)  // 设定会话时间以及重连策略
                .namespace(WORKSPACE).build();  // 设置命名空间以及开始建立连接

        // 启动Curator客户端
        client.start();
        return client;
    }

}
