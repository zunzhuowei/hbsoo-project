package com.hbsoo.game.hall.msg;

import com.hbsoo.game.commons.GameSpringBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zun.wei on 2021/9/10.
 */
@Configuration
public class HallApplicationContext {

    @Bean
    public RoomMessageListener roomMessageListener() {
        return new RoomMessageListener();
    }

//    @Bean
//    public RoomMessageInformer roomMessageInformer() {
//        return new RoomMessageInformer();
//    }


}
