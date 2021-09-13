package com.hbsoo.game.room.msg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zun.wei on 2021/9/10.
 */
@Configuration
public class RoomApplicationContext {

    @Bean
    public HallMessageInformer roomMessageListener() {
        return new HallMessageInformer();
    }

    @Bean
    public HallMessageListener roomMessageInformer() {
        return new HallMessageListener();
    }


}
