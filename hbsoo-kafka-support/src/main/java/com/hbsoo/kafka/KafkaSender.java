package com.hbsoo.kafka;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Date;

/**
 * Created by zun.wei on 2021/10/28.
 */
@Slf4j
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息
     *
     * @param topicName
     * @param key
     * @param msgObj
     */
    public void send(String topicName, String key, Object msgObj) {
        String value = msgObj instanceof String ? (String) msgObj : JSON.toJSONString(msgObj);
        sendMessage(topicName, key, value);
    }

    public void send(String topicName, Object msgObj) {
        FastDateFormat fastDateFormat = FastDateFormat.getInstance("yyyyMMdd");
        final String format = fastDateFormat.format(new Date());
        send(topicName, format, msgObj);
    }

    private void sendMessage(String topicName, String key, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, message);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.debug("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
    }

//    @PostConstruct
//    public void test() {
//        new Thread(() -> {
//            for (int i = 0; i < 100; i++) {
//                this.produce("test","key111" + i,"value111" + i);
//            }
//        }).start();
//
//    }


}
