package com.hbsoo.utils.commons;

import com.hbsoo.utils.hotswap.HotSwapClass;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by zun.wei on 2021/8/14.
 *  groovy 源码扫描器
 */
public final class GroovySrcScanner {

    static GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    /**
     * 获取目录下的所有class
     * @param dir
     * @return
     */
    public static Set<Class<?>> listClazz(String dir) {
        return listClazz(dir, (c) -> true)
                .stream().map(HotSwapClass::getClazz).collect(Collectors.toSet());
    }

    /**
     * 获取目录下，拥有某个注解的class
     * @param dir
     * @param annotationClazz
     * @return
     */
    public static Set<Class<?>> listClazz(String dir, Class<? extends Annotation>... annotationClazz) {
        return listClazz(dir, clazz -> {
            for (Class<? extends Annotation> aClass : annotationClazz) {
                return clazz.isAnnotationPresent(aClass);
            }
            return false;
        }).stream().map(HotSwapClass::getClazz).collect(Collectors.toSet());
    }

    /**
     * 获取所有子类的class
     * @param dir
     * @param superClazz
     * @return
     */
    public static Set<Class<?>> listSubClazz(String dir, Class<?> superClazz) {
        if (superClazz == null) {
            return Collections.emptySet();
        } else {
            return listClazz(dir, superClazz::isAssignableFrom)
                    .stream().map(HotSwapClass::getClazz).collect(Collectors.toSet());
        }
    }

///////////////////////////
    /**
     * 获取目录下的所有class
     * @param dir
     * @return
     */
    public static Set<HotSwapClass> listHotSwapClazz(String dir) {
        return listClazz(dir, (c) -> true);
    }

    /**
     * 获取目录下，拥有某个注解的class
     * @param dir
     * @param annotationClazz
     * @return
     */
    public static Set<HotSwapClass> listHotSwapClazz(String dir, Class<? extends Annotation>... annotationClazz) {
        return listClazz(dir, clazz -> {
            for (Class<? extends Annotation> aClass : annotationClazz) {
                return clazz.isAnnotationPresent(aClass);
            }
            return false;
        });
    }

    /**
     * 获取所有子类的class
     * @param dir
     * @param superClazz
     * @return
     */
    public static Set<HotSwapClass> listHotSwapSubClazz(String dir, Class<?> superClazz) {
        if (superClazz == null) {
            return Collections.emptySet();
        } else {
            return listClazz(dir, superClazz::isAssignableFrom);
        }
    }



    /**
     * 获取目录下包括子目录的所有 .groovy 源码文件的 class
     * @param dir 如：D://temp/groovy
     * @param filter 过滤条件
     * @return class
     */
    public static Set<HotSwapClass> listClazz(String dir, Predicate<Class<?>> filter) {
        File dirFile = new File(dir);
        // 获取子文件列表
        File[] subFileArr = dirFile.listFiles();

        // 结果对象
        Set<HotSwapClass> resultSet = new HashSet<>();
        if (subFileArr == null ||
                subFileArr.length <= 0) {
            return resultSet;
        }

        // 文件队列, 将子文件列表添加到队列
        Queue<File> fileQ = new LinkedList<>(Arrays.asList(subFileArr));

        while (!fileQ.isEmpty()) {
            // 从队列中获取文件
            File currFile = fileQ.poll();

            if (currFile.isDirectory()) {
                // 如果当前文件是目录,
                // 并且是执行递归操作时,
                // 获取子文件列表
                subFileArr = currFile.listFiles();

                if (subFileArr != null &&
                        subFileArr.length > 0) {
                    // 添加文件到队列
                    fileQ.addAll(Arrays.asList(subFileArr));
                }
                continue;
            }

            if (!currFile.isFile()) {
                // 如果当前文件不是文件,
                // 则直接跳过
                continue;
            }

            // 源文件
            if (currFile.getName().endsWith(".groovy")) {
                try {
                    final String string = FileUtils.readFileToString(currFile, "UTF-8");
                    Class<?> clazz = groovyClassLoader.parseClass(string);
                    final boolean test = filter.test(clazz);
                    if (!test) {
                        continue;
                    }
                    // 添加类定义到集合
                    HotSwapClass hotSwapClass = new HotSwapClass();
                    hotSwapClass.setClazz(clazz);
                    hotSwapClass.setSrcFileString(string);
                    resultSet.add(hotSwapClass);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return resultSet;
    }

}
