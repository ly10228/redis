package com.ly.redis.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * obj和map的转换工具
 * 
 * @author Yang JiZhou
 * @created 2017年5月25日 下午2:04:36
 */
@Slf4j
public class BeanUtils extends org.springframework.beans.BeanUtils{

    /**
     * 复制属性
     *
     * @param source
     * @param target
     * @throws BeansException
     */
    public static void copyProperties(Object source, Object target) throws BeansException {
        BeanUtils.copyProperties(source, target, null, (String[]) null);
    }

    /**
     * 复制属性
     *
     * @param source
     * @param target
     * @param editable
     * @throws BeansException
     */
    public static void copyProperties(Object source, Object target, Class<?> editable) throws BeansException {
        copyProperties(source, target, editable, (String[]) null);
    }

    /**
     * 复制属性
     *
     * @param source
     * @param target
     * @param ignoreProperties
     * @throws BeansException
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) throws BeansException {
        copyProperties(source, target, null, ignoreProperties);
    }

    /**
     * 复制属性
     *
     * @param source
     * @param target
     * @param editable
     * @param ignoreProperties
     * @throws BeansException
     */
    private static void copyProperties(Object source, Object target, Class<?> editable, String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
                        "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            org.springframework.util.ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            // 这里判断以下value是否为空 当然这里也能进行一些特殊要求的处理 例如绑定时格式转换等等
                            if (value != null) {
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            }
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * Clone 对象
     *
     * @param o
     * @param clazz
     * @param <T>
     * @return
     * @throws BeansException
     */
    public static <T> T clone(Object o, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            if (null == o)
                return null;
            copyProperties(o, t);
            return t;
        } catch (Exception e) {
            log.error("将目标转换为DTO对象，发生异常:", e);
        }
        return null;
    }

    /**
     * Close出一个List
     *
     * @param os
     * @param clazz
     * @param <T>
     * @return
     * @throws BeansException
     */
    public static <T> List<T> cloneList(List<?> os, Class<T> clazz) {
        List<T> ws = new ArrayList<>();
        if (null == os)
            return ws;
        for (Object o : os) {
            ws.add(clone(o, clazz));
        }
        return ws;
    }
}
