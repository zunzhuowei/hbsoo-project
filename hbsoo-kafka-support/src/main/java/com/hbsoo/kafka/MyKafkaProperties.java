package com.hbsoo.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by zun.wei on 2021/12/28.
 */
@Data
@ConfigurationProperties(prefix = "kafka")
public class MyKafkaProperties {

    private String bootstrapAddress = "localhost:9092";

    private Integer consumerConcurrency = 1;



}
