package com.hbsoo.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by zun.wei on 2021/12/28.
 */
@Data
@ConfigurationProperties(prefix = "kafka")
public class MyKafkaProperties {

    private String bootstrapAddress = "localhost:9092";

    private Integer consumerConcurrency = 1;

    private String groupId;


}
