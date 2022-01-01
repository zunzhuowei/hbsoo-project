package com.hbsoo.kafka.conf;

import com.hbsoo.kafka.MyKafkaProperties;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zun.wei on 2021/12/28.
 */
@Configuration
@ConditionalOnProperty(name = "kafka.enable", havingValue = "enable")
@EnableConfigurationProperties(MyKafkaProperties.class)
public class KafkaTopicConfig {


    @Autowired
    private MyKafkaProperties myKafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, myKafkaProperties.getBootstrapAddress());
        return new KafkaAdmin(configs);
    }

    /*@Bean
    public NewTopic topic1() {
        return new NewTopic("baeldung", 3, (short) 3);
    }*/

}
