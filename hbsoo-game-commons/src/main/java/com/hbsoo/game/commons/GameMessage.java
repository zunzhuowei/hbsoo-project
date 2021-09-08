package com.hbsoo.game.commons;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zun.wei on 2021/8/31.
 */
@Data
public class GameMessage implements Serializable {

    /**
     * 消息类型
     */
    private int msgType;

    /**
     * 传输数据json对象
     */
    private String dataJson;

    /**
     * 是否为批量对象
     */
    private boolean batch = false;


    public <T> T getData(Class<T> clazz) {
        return JSON.parseObject(this.dataJson, clazz);
    }

    public <T> List<T> getDataList(Class<T> clazz) {
        return JSON.parseArray(this.dataJson, clazz);
    }

}
