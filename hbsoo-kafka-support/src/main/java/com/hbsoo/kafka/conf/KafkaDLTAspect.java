package com.hbsoo.kafka.conf;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.record.TimestampType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * Created by zun.wei on 2021/10/29.
 */
@Aspect
public class KafkaDLTAspect {

    private static final Logger log = LoggerFactory.getLogger(KafkaDLTAspect.class);


    // 配置织入点
    @Pointcut("@annotation(com.hbsoo.kafka.annotations.EnableDLT)")
    public void dltPointCut() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "dltPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        //handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "dltPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        Object args = joinPoint.getArgs();
        if (Objects.nonNull(args)) {
            String params = argsArrayToString(joinPoint.getArgs());
            log.error("KafkaDLTAspect doAfterThrowing --::{},{}", params, e.getMessage());
        }
        //e.printStackTrace();
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                if (Objects.nonNull(paramsArray[i]) && paramsArray[i] instanceof ConsumerRecord) {
                    ConsumerRecord<String, String> record = (ConsumerRecord<String, String>) paramsArray[i];
                    String key = record.key();
                    String value = record.value();
                    long offset = record.offset();
                    long timestamp = record.timestamp();
                    String topic = record.topic();
                    int partition = record.partition();
                    TimestampType timestampType = record.timestampType();

                    /*KafkaErrLog kafkaErrLog = new KafkaErrLog();
                    kafkaErrLog.setKey(key);
                    kafkaErrLog.setValue(value);
                    kafkaErrLog.setOffset(offset);
                    kafkaErrLog.setTimestamp(timestamp);
                    kafkaErrLog.setTopic(topic);
                    kafkaErrLog.setPartition(partition);
                    kafkaErrLog.setTimestampType(timestampType);
                    String jsonObj = JSONObject.toJSONString(kafkaErrLog);
                    params.append(jsonObj).append("\n");
                    kafkaErrLogService.save(kafkaErrLog);*/
                }
            }
        }
        return params.toString();
    }

}
