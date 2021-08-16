package com.hbsoo.utils;

import groovy.lang.GroovyClassLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by zun.wei on 2021/8/14.
 *  groovy 源码扫描器
 */
public final class GroovySrcScanner {

    static GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    public static Set<Class<?>> listClazz(String dir) {
        return listClazz(dir, (c) -> true);
    }

    public static Set<Class<?>> listClazz(String packageName, Class<? extends Annotation>... annotationClazz) {
        return listClazz(packageName, clazz -> {
            for (Class<? extends Annotation> aClass : annotationClazz) {
                return clazz.isAnnotationPresent(aClass);
            }
            return false;
        });
    }

    /**
     * 获取目录下包括子目录的所有 .groovy 源码文件的 class
     * @param dir 如：D://temp/groovy
     * @param filter 过滤条件
     * @return class
     */
    public static Set<Class<?>> listClazz(String dir, Predicate<Class<?>> filter) {
        File dirFile = new File(dir);
        // 获取子文件列表
        File[] subFileArr = dirFile.listFiles();

        // 结果对象
        Set<Class<?>> resultSet = new HashSet<>();
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
                    resultSet.add(clazz);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

//        final ClassLoader parent = groovyClassLoader.getParent();
//        try {
//            Vector<Class<?>> classes = (Vector<Class<?>>) ReflectionUtils
//                    .getPrivateField(parent, "classes");
//            final Class[] loadedClasses = groovyClassLoader.getLoadedClasses();
//            for (Class loadedClass : loadedClasses) {
//                final String groovyClassName = loadedClass.getName();
//                classes.removeIf(e -> StringUtils.equals(e.getName(), groovyClassName));
//                classes.addElement(loadedClass);
//                //System.out.println("loadedClass = " + loadedClass);
//            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }


        return resultSet;
    }

}
