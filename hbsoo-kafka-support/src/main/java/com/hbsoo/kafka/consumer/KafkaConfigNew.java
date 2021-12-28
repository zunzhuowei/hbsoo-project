//package com.hbsoo.kafka.consumer;
//
//import com.alibaba.fastjson.JSON;
//import com.hbsoo.kafka.MyKafkaProperties;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//import java.time.Duration;
//import java.util.Collections;
//import java.util.Properties;
//
///**
// * Created by zun.wei on 2021/12/28.
// */
//@Configuration
//@Slf4j
//public class KafkaConfigNew {
//
//    @Autowired
//    private Environment environment;
//
//    @Autowired
//    private MyKafkaProperties myKafkaProperties;
//
//    @Value("${spring.profiles.active:local}")
//    private String env;
//
//    /*
//    enable.auto.commit=true
//    auto.commit.interval.ms=1000
//     */
//    @Bean
//    public void loadKafkaConfig() {
//        Properties p = new Properties();
//        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, myKafkaProperties.getBootstrapAddress());
//        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, environment.getProperty("enable.auto.commit"));
//        p.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, environment.getProperty("auto.commit.interval.ms"));
//        p.put(ConsumerConfig.GROUP_ID_CONFIG, myKafkaProperties.getGroupId() + "-" + env);
//
//        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(p);
//        kafkaConsumer.subscribe(Collections.singletonList(environment.getProperty("kafkaTopicName")));// 订阅消息
//        log.info("消息订阅成功！kafka配置：" + p.toString());
//        //启动消息监听线程
//        new Thread(() -> {
//            //进行消息监听
//            while (true) {
//                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10));
//                //log.info("poll数据：" + JSON.toJSONString(records));
//                for (ConsumerRecord<String, String> record : records) {
//                    try {
//                        //kafkaConsumerListener.listen(record);
//                        log.info("poll数据：" + JSON.toJSONString(records));
//                    } catch (Exception e) {
//                        log.error("消息消费异常！", e);
//                    }
//                }
//            }
//        }).start();
//    }
//
//}
