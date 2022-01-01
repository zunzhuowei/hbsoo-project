package com.hbsoo.kafka.conf;

import com.hbsoo.kafka.MyKafkaProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zun.wei on 2021/12/28.
 */
@EnableKafka
@Configuration
@ConditionalOnProperty(name = "kafka.enable", havingValue = "enable")
@EnableConfigurationProperties(MyKafkaProperties.class)
public class KafkaConsumerConfig {


    @Autowired
    private MyKafkaProperties myKafkaProperties;

    @Value("${spring.profiles.active:local}")
    private String env;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                myKafkaProperties.getBootstrapAddress());
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                myKafkaProperties.getGroupId() + "-" + env);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(myKafkaProperties.getConsumerConcurrency());
        factory.getContainerProperties().setPollTimeout(30000);
        //factory.setBatchListener(true);//设置为批量消费，每个批次数量在Kafka配置参数中设置
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//设置手动提交ackMode
        return factory;
    }

}
