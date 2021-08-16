package com.hbsoo.utils.commons;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zun.wei on 2021/8/14.
 */
public final class ReflectionUtils {

    /**
     * 获取私有成员变量的值
     * @param instance
     * @param filedName
     * @return
     */
    public static Object getPrivateField(Object instance, String filedName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(filedName);
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * 设置私有成员的值
     * @param instance
     * @param fieldName
     * @param value
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void setPrivateField(Object instance, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    /**
     * 访问私有方法
     * @param instance
     * @param methodName
     * @param classes
     * @param objects
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object invokePrivateMethod(Object instance, String methodName, Class[] classes, String objects)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = instance.getClass().getDeclaredMethod(methodName, classes);
        method.setAccessible(true);
        return method.invoke(instance, objects);
    }


//    private String name;
//
//    private void setName(String name) {
//        this.name = name;
//    }
//
//    public void test() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        ReflectionUtils reflectionUtilsTest = new ReflectionUtils();
//        //访问私有属性
//        System.out.println("name = " + ReflectionUtils.getPrivateField(reflectionUtilsTest, "name"));
//        //设置私有属性
//        ReflectionUtils.setPrivateField(reflectionUtilsTest, "name", "张三");
//        System.out.println("name = " + ReflectionUtils.getPrivateField(reflectionUtilsTest, "name"));
//        //调用私有方法
//        ReflectionUtils.invokePrivateMethod(reflectionUtilsTest, "setName", new Class[]{String.class}, "李四");
//        System.out.println("name = " + ReflectionUtils.getPrivateField(reflectionUtilsTest, "name"));
//    }

}
