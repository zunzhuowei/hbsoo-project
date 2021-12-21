package com.hbsoo.utils.zoo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;

/**
 * Created by zun.wei on 2021/12/21.
 */
public class ZooTest {


    public static void main(String[] args) throws Exception {
        //connectionString 	服务器列表，格式host1:port1,host2:port2,...
        String connectionInfo = "192.168.1.215:2181,192.168.1.215:2182,192.168.1.215:2183";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(connectionInfo)
                        .sessionTimeoutMs(5000)
                        .connectionTimeoutMs(5000)
                        .retryPolicy(retryPolicy)
                        //.namespace("base")
                        .build();
        client.start();

        //client.create().withMode(CreateMode.EPHEMERAL).forPath("/path","aaaa".getBytes(StandardCharsets.UTF_8));
        client.create().withMode(CreateMode.PERSISTENT).forPath("/path111","aaaa".getBytes(StandardCharsets.UTF_8));
        //final byte[] bytes = client.getData().forPath("/");
        //System.out.println("bytes = " + bytes);
    }

}
