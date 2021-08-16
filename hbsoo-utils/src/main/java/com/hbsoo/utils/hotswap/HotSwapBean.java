package com.hbsoo.utils.hotswap;

/**
 * Created by zun.wei on 2021/8/16.
 */
public final class HotSwapBean {

    /**
     * 源文件md5字符串
     */
    private String srcFileMd5;

    /**
     * bean 对象
     */
    private Object bean;

    /**
     * bean 名称，全限定名
     */
    private String beanName;

    /**
     * bean 名称
     */
    private String beanSimpleName;

    /**
     * bean class
     */
    private Class<?> clazz;


    public String getSrcFileMd5() {
        return srcFileMd5;
    }

    public void setSrcFileMd5(String srcFileMd5) {
        this.srcFileMd5 = srcFileMd5;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanSimpleName() {
        return beanSimpleName;
    }

    public void setBeanSimpleName(String beanSimpleName) {
        this.beanSimpleName = beanSimpleName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
