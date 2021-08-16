package com.hbsoo.utils.hotswap;

/**
 * Created by zun.wei on 2021/8/16.
 */
public final class HotSwapClass {

    private String srcFileString;

    private Class<?> clazz;

    public String getSrcFileString() {
        return srcFileString;
    }

    public void setSrcFileString(String srcFileString) {
        this.srcFileString = srcFileString;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
