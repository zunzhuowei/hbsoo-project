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

    /**
     * 热更对象缓存；key (class name); value (HotSwapBean)
     */
    private static final Map<String, HotSwapBean> hotSwapBeans = new ConcurrentHashMap<>();
    /**
     * 热更对象缓存；key (interface class name); value (Set<HotSwapBean>)
     */
    private static final Map<String, Set<HotSwapBean>> hotSwapInterfaces = new ConcurrentHashMap<>();

    /**
     * 添加或者更新热更对象
     * @param hotSwapClasses 热更对象
     */
    public static void addOrUpdateHotSwapBean(HotSwapClass... hotSwapClasses) {
        Set<HotSwapClass> set = new HashSet<>(Arrays.asList(hotSwapClasses));
        addOrUpdateHotSwapBeans(set);
    }

    /**
     * 获取class 的所有接口 class
     * @param interfacesClasses 用来装class 的容器
     * @param clazz 想要获取的class的所有接口
     * @return interfacesClasses
     */
    private static Set<Class<?>> getClazzAllInterfaces(Set<Class<?>> interfacesClasses, Class<?> clazz) {
        final Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (anInterface == GroovyObject.class) {
                continue;
            }
            interfacesClasses.add(anInterface);
        }
        final Class<?> superclass = clazz.getSuperclass();
        if (superclass == Object.class) {
            return interfacesClasses;
        }
        return getClazzAllInterfaces(interfacesClasses, superclass);
    }

    /**
     * 热更对象是否有变化
     * @param hotSwapClasses 热更对象
     * @return true 有变化，false 没有变化
     */
    private static boolean hasChange(Collection<HotSwapClass> hotSwapClasses) {
        if (hotSwapBeans.isEmpty()) {
            return true;
        }
        for (HotSwapClass hotSwapClass : hotSwapClasses) {
            final Class<?> clazz = hotSwapClass.getClazz();
            if (clazz.isInterface()) {
                continue;
            }
            final HotSwapBean hotSwapBean = hotSwapBeans.get(clazz.getName());
            if (Objects.isNull(hotSwapBean)) {
                return true;
            }
            final String srcFileMd5 = hotSwapBean.getSrcFileMd5();
            final String srcFileString = hotSwapClass.getSrcFileString();
            final String newSrcMd5Str = DigestUtils.md5DigestAsHex(srcFileString.getBytes(StandardCharsets.UTF_8));
            if (!StringUtils.equals(srcFileMd5, newSrcMd5Str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加或者更新热更对象
     * @param hotSwapClasses 热更对象
     */
    public static void addOrUpdateHotSwapBeans(Collection<HotSwapClass> hotSwapClasses) {
        final boolean b = hasChange(hotSwapClasses);
        if (!b) {
            return;
        }
        hotSwapBeans.clear();
        hotSwapInterfaces.clear();

        try {
            for (HotSwapClass hotSwapClass : hotSwapClasses) {
                final Class<?> clazz = hotSwapClass.getClazz();
                if (clazz.isInterface()) {
                    continue;
                }
                Set<Class<?>> interfacesClasses = new HashSet<>();
                getClazzAllInterfaces(interfacesClasses, clazz);
                final Class<?>[] interfaces = interfacesClasses.toArray(new Class<?>[0]);

                final String srcFileString = hotSwapClass.getSrcFileString();
                final String newSrcMd5Str = DigestUtils.md5DigestAsHex(srcFileString.getBytes(StandardCharsets.UTF_8));
                HotSwapBean hotSwapBean = new HotSwapBean();
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

    public static <T> Set<T> getHotSwapBeans(Class<T> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            return null;
        }
        final Set<HotSwapBean> hotSwapBeans = hotSwapInterfaces.get(interfaceClass.getName());
        if (Objects.isNull(hotSwapBeans) || hotSwapBeans.isEmpty()) {
            return null;
        }
        return hotSwapBeans.stream()
                .map(e -> {
                    Object o = e.getBean();
                    return (T) o;
                })
                .collect(Collectors.toSet());
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
                return new ArrayList<>();
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
            return new ArrayList<>();
        }
        T t = (T) hotSwapBean.getBean();
        if (t.getClass().isAnnotationPresent(annotationType)) {
            return Collections.singletonList(t);
        }
        return new ArrayList<>();
    }

    /**
     * 处理接口类型的热更对象
     * @param anInterface 接口class
     * @param srcMd5Str 源文件md5值
     * @param obj 接口实现类对象
     */
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
