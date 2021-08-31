package com.hbsoo.commons.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by zun.wei on 2021/8/31.
 */
@Data
public class GameMessage implements Serializable {

    private int msgType;

    private String dataJson;


    public <T> T getData(Class<T> clazz) {
        return JSON.parseObject(this.dataJson, clazz);
    }


}
