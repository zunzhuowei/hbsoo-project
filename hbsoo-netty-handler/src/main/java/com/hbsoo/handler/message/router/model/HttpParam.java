package com.hbsoo.handler.message.router.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zun.wei on 2021/7/31.
 */
public final class HttpParam {

    private Map<String, Object> paramsMap = new HashMap<>();

    public void setParamsMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }

    /**
     * 获取 字符串值
     * @param key 键
     * @return 字符串值
     */
    public String get(String key) {
        final Object o = paramsMap.get(key);
        if (o instanceof List) {
            return ((List<String>) o).get(0);
        } else {
            return (String) o ;
        }

    }

    /**
     * 获取 列表列表
     * @param key 键
     * @return 字符串值列表
     */
    public List<String> getList(String key) {
        final Object o = paramsMap.get(key);
        if (o instanceof List) {
            return (List<String>) paramsMap.get(key);
        } else {
            String valueStr = (String) paramsMap.get(key);
            return Collections.singletonList(valueStr);
        }
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    @Override
    public String toString() {
        return "HttpParam{" +
                "paramsMap=" + paramsMap +
                '}';
    }
}
