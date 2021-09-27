package com.hbsoo.http;

import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zun.wei on 2021/9/27.
 */
@Configuration
@EnableConfigurationProperties(HttpToolProperties.class)
public class HttpToolConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        //new OkHttpClient.Builder().
        OkHttpClient okHttpClient = new OkHttpClient();

        return okHttpClient;
    }

}
