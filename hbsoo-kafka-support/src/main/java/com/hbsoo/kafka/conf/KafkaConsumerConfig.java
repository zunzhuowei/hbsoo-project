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
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300_000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 1);
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 52428800);//50M
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    consumeContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(myKafkaProperties.getConsumerConcurrency());
        factory.getContainerProperties().setPollTimeout(1000);//设置阻止消费者等待记录的最长时间
        //factory.setBatchListener(true);//设置为批量消费，每个批次数量在Kafka配置参数中设置
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);//设置手动提交ackMode
        return factory;
    }

    /*
 下面6个参数是3对，通俗理解如下：

 1，2配合使用，告诉kafka集群，我消费者的处理能力，每秒至少能消费掉

 3，4配合使用，告诉kafka集群，在我没事情干的时候，多久尝试拉取一次数据，即使此时没有数据（所以要处理空消息）

 5，6配合使用，告诉kafka集群，什么情况你可以认为整个消费者挂了，触发rebanlance

编号 参数名	含义	 默认值	备注
(1) max.poll.interval.ms	拉取时间间隔	300s	每次拉取的记录必须在该时间内消费完
(2) max.poll.records	每次拉取条数	500条	这个条数一定要结合业务背景合理设置
(3) fetch.max.wait.ms	每次拉取最大等待时间		时间达到或者消息大小谁先满足条件都触发，没有消息但时间达到返回空消息体
(4) fetch.min.bytes	每次拉取最小字节数		时间达到或者消息大小谁先满足条件都触发
(5) heartbeat.interval.ms	向协调器发送心跳的时间间隔	3s	建议不超过session.timeout.ms的1/3
(6) session.timeout.ms	心跳超时时间	30s	配置太大会导致真死消费者检测太慢
     */
}
