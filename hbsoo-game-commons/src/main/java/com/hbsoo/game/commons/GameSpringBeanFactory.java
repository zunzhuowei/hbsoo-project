package com.hbsoo.game.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
//@Component
public final class GameSpringBeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;
    private static AutowireCapableBeanFactory autowireCapableBeanFactory;

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
        autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
    }


    public static void autowireBean(Class<?> clazz) {
        final String simpleName = clazz.getSimpleName();
        String beanNamePrefix = simpleName.substring(0, 1).toLowerCase();
        String beanNameSuffix = simpleName.substring(1);
        String beanName = beanNamePrefix + beanNameSuffix;
        AnnotationConfigApplicationContext applicationContext = (AnnotationConfigApplicationContext) context;
        addBean(applicationContext, beanName, clazz);

        //final int beanDefinitionCount = context.getBeanDefinitionCount();
        //System.out.println("beanDefinitionCount = " + beanDefinitionCount);
    }


    /**
     * 向容器中动态添加Bean
     *
     * @param ctx
     * @param beanName
     * @param beanClass
     */
    private static void addBean(AbstractApplicationContext ctx, String beanName, Class beanClass) {
        BeanDefinitionRegistry beanDefReg = (DefaultListableBeanFactory) ctx.getBeanFactory();
        BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        BeanDefinition beanDef = beanDefBuilder.getBeanDefinition();
        if (beanDefReg.containsBeanDefinition(beanName)) {
            removeBean(ctx, beanName);
        }

        if (!beanDefReg.containsBeanDefinition(beanName)) {
            beanDefReg.registerBeanDefinition(beanName, beanDef);
        }
    }



    /**
     * 从容器中移除Bean
     *
     * @param ctx
     * @param beanName
     */
    private static void removeBean(AbstractApplicationContext ctx, String beanName) {
        BeanDefinitionRegistry beanDefReg = (DefaultListableBeanFactory) ctx.getBeanFactory();
        beanDefReg.getBeanDefinition(beanName);
        beanDefReg.removeBeanDefinition(beanName);
    }


}
