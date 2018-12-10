package com.suansuan.sframework.utils.adr.ui;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.ContentValues.TAG;

/**
 * Created by wzx on 2018/8/17.
 */
@SuppressWarnings("all")
public class StatusBarTextColorHelper {

    /**
     * 设置状态栏字体颜色
     * 只允许设置字体深色（需要6.0或以上版本支持）或者浅色
     * 小米MIUI6以上支持 魅族Flyme4.0以上支持
     *
     * @param activity Activity
     * @param isLight  是否浅色
     */
    public static boolean setStatusBarTextColor(Activity activity, boolean isLight) {
        boolean customSetSucc = false;
        if (OSUtils.isMiui()) {
            customSetSucc = setMIUI(activity, isLight);
        } else if (OSUtils.isFlyme()) {
            customSetSucc = setFlymeUI(activity, isLight);
        }
        boolean normalSetSucc = setStatusBarTextColorNormal(activity, isLight);
        return normalSetSucc || customSetSucc;
    }

    /**
     * 需要MIUIV6以上
     *
     * @param activity
     * @param isLight  是否把状态栏文字及图标颜色设置为浅色
     * @return boolean 成功执行返回true
     */
    private static boolean setMIUI(Activity activity, boolean isLight) {
        try {
            Window window = activity.getWindow();
            Class clazz = window.getClass();
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (isLight) {
                extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
            } else {
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 魅族字体设置
     *
     * @param activity
     * @param isLight
     */
    private static boolean setFlymeUI(Activity activity, boolean isLight) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (isLight) {
                value &= ~bit;
            } else {
                value |= bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置状态栏字体颜色
     * 只允许设置字体深色（需要6.0或以上版本支持）或者浅色
     *
     * @param activity Activity
     * @param isLight  是否浅色
     */
    private static boolean setStatusBarTextColorNormal(Activity activity, boolean isLight) {
        if (isLight) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            return true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                return true;
            }
        }
        return false;
    }

    private static class OSUtils {
        public static final String ROM_MIUI = "MIUI";
        public static final String ROM_FLYME = "FLYME";
        private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";

        private static String sName;
        private static String sVersion;

        /**
         * 判断手机是否是Miui
         */
        public static boolean isMiui() {
            return check(ROM_MIUI);
        }

        /**
         * 判断手机是否是Flyme
         */
        public static boolean isFlyme() {
            return check(ROM_FLYME);
        }

        public static boolean check(String rom) {
            if (sName != null) {
                return sName.equals(rom);
            }
            if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
                sName = ROM_MIUI;
            } else {
                sVersion = Build.DISPLAY;
                if (sVersion.toUpperCase().contains(ROM_FLYME)) {
                    sName = ROM_FLYME;
                } else {
                    sVersion = Build.UNKNOWN;
                    sName = Build.MANUFACTURER.toUpperCase();
                }
            }
            return sName.equals(rom);
        }

        public static String getProp(String name) {
            String line = null;
            BufferedReader input = null;
            try {
                Process p = Runtime.getRuntime().exec("getprop " + name);
                input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
                line = input.readLine();
                input.close();
            } catch (IOException ex) {
                Log.e(TAG, "Unable to read prop " + name, ex);
                return null;
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return line;
        }
    }
}
