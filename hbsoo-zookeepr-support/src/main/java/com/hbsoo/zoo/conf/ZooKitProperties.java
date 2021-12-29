package com.hbsoo.zoo.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by zun.wei on 2021/12/29.
 */
@Data
@ConfigurationProperties(prefix = "zookeeper")
public class ZooKitProperties {


    private String listOfServers;

}
