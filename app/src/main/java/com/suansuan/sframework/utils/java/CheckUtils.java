package com.suansuan.sframework.utils.java;

import java.util.Collection;
import java.util.Map;

/**
 * 检查相关的Utils
 */
@SuppressWarnings("all")
public class CheckUtils {

    /**
     * 检查任意Object是否为空
     * <hr>
     * shallow check : 不会检查容器内部的元素是否为空
     * @return
     *          true：为null
     *          false：不为null
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Collection<?>) {
            // 检查各种Collection是否为空(List,Queue,Set)
            return ((Collection<?>) obj).isEmpty();
        } else if (obj instanceof Map<?, ?>) {
            // 检查各种Map
            return ((Map<?, ?>) obj).isEmpty();
        } else if (obj instanceof CharSequence) {
            // 检查各种CharSequence
            return ((CharSequence) obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            // 检查各种base array
            // return Array.getLength(obj) == 0;
            if (obj instanceof Object[]) {
                return ((Object[]) obj).length == 0;
            } else if (obj instanceof int[]) {
                return ((int[]) obj).length == 0;
            } else if (obj instanceof long[]) {
                return ((long[]) obj).length == 0;
            } else if (obj instanceof short[]) {
                return ((short[]) obj).length == 0;
            } else if (obj instanceof double[]) {
                return ((double[]) obj).length == 0;
            } else if (obj instanceof float[]) {
                return ((float[]) obj).length == 0;
            } else if (obj instanceof boolean[]) {
                return ((boolean[]) obj).length == 0;
            } else if (obj instanceof char[]) {
                return ((char[]) obj).length == 0;
            } else if (obj instanceof byte[]) {
                return ((byte[]) obj).length == 0;
            }
        }
        return false;
    }

    /**
     * 取反{@link #isEmpty(Object obj)},大量情况下可以少写一个感叹号
     */
    public static boolean isExist(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 检查容器是否为null，或者容器内的元素是否为null
     * @param objs
     * @param <T>
     * @return
     */
    public static <T> boolean isContainsEmpty(T... objs) {
        if (isEmpty(objs)) {
            return true;
        }
        for (T obj : objs) {
            if (isEmpty(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否为奇数
     * @param i
     * @return
     */
    public static boolean isOdd(int i) {
        return i % 2 != 0;
    }

    /**
     * 是否为偶数
     * @param i
     * @return
     */
    public static boolean isEven(int i) {
        return i % 2 == 0;
    }

    /**
     * 检查枚举组中是否包含指定枚举
     * @param group
     * @param child 不能为空
     * @return
     */
    public static boolean isContainsEnum(Enum<?>[] group, Enum<?> child) {
        if (isEmpty(group)) {
            return false;
        }
        for (Enum<?> enums : group) {
            if (enums == child) {
                return true;
            }
        }
        return false;
    }

}
