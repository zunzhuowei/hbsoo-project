package com.hbsoo.utils.hotswap;


import com.hbsoo.utils.commons.DigestUtils;
import groovy.lang.GroovyObject;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by zun.wei on 2021/8/16.
 */
public final class HotSwapHolder {


    private static final Map<String, HotSwapBean> hotSwapBeans = new ConcurrentHashMap<>();
    private static final Map<String, Set<HotSwapBean>> hotSwapInterfaces = new ConcurrentHashMap<>();

    public static void addOrUpdateHotSwapBean(HotSwapClass... hotSwapClasses) {
        Set<HotSwapClass> set = new HashSet<>(Arrays.asList(hotSwapClasses));
        addOrUpdateHotSwapBeans(set);
    }

    public static void addOrUpdateHotSwapBeans(Collection<HotSwapClass> hotSwapClasses) {
        try {
            for (HotSwapClass hotSwapClass : hotSwapClasses) {
                final Class<?> clazz = hotSwapClass.getClazz();
                if (clazz.isInterface()) {
                    continue;
                }

                final Class<?>[] interfaces = clazz.getInterfaces();
                //GroovyObject

                final String srcFileString = hotSwapClass.getSrcFileString();
                final String newSrcMd5Str = DigestUtils.md5DigestAsHex(srcFileString.getBytes(StandardCharsets.UTF_8));

                HotSwapBean hotSwapBean = hotSwapBeans.get(clazz.getName());
                if (Objects.nonNull(hotSwapBean)) {
                    final String srcFileMd5 = hotSwapBean.getSrcFileMd5();
                    if (StringUtils.equals(newSrcMd5Str, srcFileMd5)) {
                        continue;
                    }
                    final Object instance = clazz.newInstance();
                    hotSwapBean.setBean(instance);
                    hotSwapBean.setSrcFileMd5(newSrcMd5Str);
                    hotSwapBean.setBeanName(clazz.getName());
                    hotSwapBean.setBeanSimpleName(clazz.getSimpleName());
                    hotSwapBean.setClazz(clazz);
                    hotSwapBeans.put(clazz.getName(), hotSwapBean);

                    for (Class<?> anInterface : interfaces) {
                        if (anInterface == GroovyObject.class) {
                            continue;
                        }
                        handlerInterfaces(anInterface, hotSwapBean.getSrcFileMd5(), instance);
                    }
                } else {
                    hotSwapBean = new HotSwapBean();
                    final Object instance = clazz.newInstance();
                    hotSwapBean.setBean(instance);
                    hotSwapBean.setSrcFileMd5(newSrcMd5Str);
                    hotSwapBean.setBeanName(clazz.getName());
                    hotSwapBean.setBeanSimpleName(clazz.getSimpleName());
                    hotSwapBean.setClazz(clazz);
                    hotSwapBeans.put(clazz.getName(), hotSwapBean);

                    for (Class<?> anInterface : interfaces) {
                        if (anInterface == GroovyObject.class) {
                            continue;
                        }
                        handlerInterfaces(anInterface, hotSwapBean.getSrcFileMd5(), instance);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> T newEntity(Class<T> clazz) {
        if (clazz.isInterface()) {
            final Set<HotSwapBean> hotSwapBeans = hotSwapInterfaces.get(clazz.getName());
            if (Objects.isNull(hotSwapBeans) || hotSwapBeans.isEmpty()) {
                return null;
            }
            final HotSwapBean hotSwapBean = hotSwapBeans.stream().findFirst().get();
            final Class<?> clazz1 = hotSwapBean.getClazz();
            try {
                return (T) clazz1.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        final HotSwapBean hotSwapBean = hotSwapBeans.get(clazz.getName());
        if (Objects.isNull(hotSwapBean)) {
            return null;
        }
        final Class<?> clazz1 = hotSwapBean.getClazz();
        try {
            return (T) clazz1.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getHotSwapBean(Class<T> clazz) {
        if (clazz.isInterface()) {
            final Set<HotSwapBean> hotSwapBeans = hotSwapInterfaces.get(clazz.getName());
            if (Objects.isNull(hotSwapBeans) || hotSwapBeans.isEmpty()) {
                return null;
            }
            final HotSwapBean hotSwapBean = hotSwapBeans.stream().findFirst().get();
            return (T) hotSwapBean.getBean();
        }

        final HotSwapBean hotSwapBean = hotSwapBeans.get(clazz.getName());
        if (Objects.isNull(hotSwapBean)) {
            return null;
        }
        return (T) hotSwapBean.getBean();
    }

    public static <T> List<T> getHotSwapBean(Class<T> clazz, Class<? extends Annotation> annotationType) {
        if (clazz.isInterface()) {
            final Set<HotSwapBean> hotSwapBeans = hotSwapInterfaces.get(clazz.getName());
            if (Objects.isNull(hotSwapBeans) || hotSwapBeans.isEmpty()) {
                return null;
            }
            return hotSwapBeans.stream()
                    .filter(e -> {
                        Class<?> c = e.getClazz();
                        return c.isAnnotationPresent(annotationType);
                    })
                    .map(HotSwapBean::getBean)
                    .map(e -> (T) e)
                    .collect(Collectors.toList());
        }

        final HotSwapBean hotSwapBean = hotSwapBeans.get(clazz.getName());
        if (Objects.isNull(hotSwapBean)) {
            return null;
        }
        T t = (T) hotSwapBean.getBean();
        if (t.getClass().isAnnotationPresent(annotationType)) {
            return Arrays.asList(t);
        }
        return null;
    }


    private static void handlerInterfaces(Class<?> anInterface, String srcMd5Str, Object obj) {
        Set<HotSwapBean> hotSwapBeans = hotSwapInterfaces.get(anInterface.getName());
        if (Objects.isNull(hotSwapBeans)) {
            hotSwapBeans = new HashSet<>();
        }

        hotSwapBeans.removeIf(e -> {
            final String srcFileMd5 = e.getSrcFileMd5();
            if (StringUtils.equals(srcMd5Str, srcFileMd5)) {
                return false;
            }
            return StringUtils.equals(obj.getClass().getName(), e.getBeanName());
        });

        final Optional<HotSwapBean> optional = hotSwapBeans.stream()
                .filter(e -> StringUtils.equals(e.getBeanName(), obj.getClass().getName()))
                .findFirst();
        if (!optional.isPresent()) {
            HotSwapBean hotSwapBean = new HotSwapBean();
            hotSwapBean.setBean(obj);
            hotSwapBean.setSrcFileMd5(srcMd5Str);
            hotSwapBean.setBeanName(obj.getClass().getName());
            hotSwapBean.setBeanSimpleName(obj.getClass().getSimpleName());
            hotSwapBean.setClazz(obj.getClass());
            hotSwapBeans.add(hotSwapBean);
            hotSwapInterfaces.put(anInterface.getName(), hotSwapBeans);
        }

    }

}
