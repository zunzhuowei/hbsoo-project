package com.hbsoo.handler.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
//@Component
public final class SpringBeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T> T getBean(Class<T> c) {
        return context.getBean(c);
    }


    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    /**
     * 通过name获取 Bean
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return context.getBeansWithAnnotation(annotationType);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    /**
     * 根据类型获取并且制定bean上必须有某个annotation
     * @param clazz bean class
     * @param annotationType annotation
     * @param <T> bean type
     * @return  bean
     */
    public static <T> List<T> getBeansOfTypeWithAnnotation(Class<T> clazz, Class<? extends Annotation> annotationType) {
        final Map<String, T> beansOfType = getBeansOfType(clazz);
        if (!beansOfType.isEmpty()) {
            return beansOfType.values().parallelStream()
                    .filter(e -> e.getClass().isAnnotationPresent(annotationType))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }


}
