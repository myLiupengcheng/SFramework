package com.suansuan.sframework.utils.adr;

import android.os.Build;
import android.util.SparseArray;
import android.view.View;

/**
 * Android 版本常用工具
 */
@SuppressWarnings("all")
public class VersionUtils {

    static int version = android.os.Build.VERSION.SDK_INT;

    public static int getSDKVersion() {
        return version;
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return getSDKVersion() >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return getSDKVersion() >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return getSDKVersion() >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return getSDKVersion() >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return getSDKVersion() >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static void setObjectToTag(int id, View view, Object obj) {
        if (hasHoneycomb()) {
            view.setTag(id, obj);
        } else {
            Object tag = view.getTag();
            if (!SparseArray.class.isInstance(tag)) {
                tag = new SparseArray<Object>();
                view.setTag(tag);
            }
            ((SparseArray<Object>) tag).put(id, obj);
            view.setTag(tag);
        }
    }

    public static Object getObjectFromTag(int id, View view) {
        if (hasHoneycomb()) {
            return view.getTag(id);
        } else {
            Object tag = view.getTag();
            if (!SparseArray.class.isInstance(tag)) {
                return null;
            }
            return ((SparseArray<Object>) tag).get(id);
        }
    }
}
