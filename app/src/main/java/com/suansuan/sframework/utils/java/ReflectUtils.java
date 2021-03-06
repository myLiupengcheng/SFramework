/**
 * Copyright © 2014 Qunar.com Inc. All Rights Reserved.
 */
package com.suansuan.sframework.utils.java;

import android.util.Log;

import java.lang.reflect.Method;

@SuppressWarnings("all")
public class ReflectUtils {

    public static Method getMethod(Class<?> clazz, String mName, Class<?>[] paramType) {
        Method m = null;
        while (clazz != null) {
            try {
                m = clazz.getDeclaredMethod(mName, paramType);
            } catch (Exception ignored) {
            }
            if (m != null) {
                m.setAccessible(true);
                break;
            }
            clazz = clazz.getSuperclass();
        }
        return m;
    }


    /**
     * 反射静态方法
     *
     * @param mName       方法名
     * @param paramType   参数类型
     * @param paramValues 值
     * @return Object
     * @since 2014年1月21日下午4:35:42
     */
    public static Object invokeStaticMethod(String className, String mName, Class<?>[] paramType, Object[] paramValues) {
        try {
            Class<?> objClz = Class.forName(className);
            Method method = getMethod(objClz, mName, paramType);
            return method.invoke(null, paramValues);
        } catch (Exception e) {
            Log.e("suansuan","reflect failed :" + e);
        }
        return null;
    }


    /**
     * 判断某个方法是否存在
     */
    public static boolean isMethodExit(String className, String mName, Class<?>[] paramType) {
        try {
            Class<?> objClz = Class.forName(className);
            objClz.getDeclaredMethod(mName, paramType);
        } catch (Exception exception) {
            //判处异常表示 不存在 该类 或 方法
            return false;
        }

        return true;
    }

}
