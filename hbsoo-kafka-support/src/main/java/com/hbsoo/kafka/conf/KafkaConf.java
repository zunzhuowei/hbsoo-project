package com.hbsoo.kafka.conf;

import com.hbsoo.kafka.KafkaSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zun.wei on 2021/12/30.
 */
@Configuration
public class KafkaConf {

    @Bean
    public KafkaSender kafkaSender() {
        return new KafkaSender();
    }

    @Bean
    public KafkaDLTAspect kafkaDLTAspect() {
        return new KafkaDLTAspect();
    }

}
