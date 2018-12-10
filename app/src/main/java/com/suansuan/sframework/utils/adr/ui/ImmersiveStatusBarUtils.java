package com.suansuan.sframework.utils.adr.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * 沉浸式相应的工具类
 */
@SuppressWarnings("all")
public class ImmersiveStatusBarUtils {

    // essential适配有问题
    private static String[] unImmersiveBrandArr = new String[]{"essential"};

    /**
     * 是否需要沉浸式适配
     *
     * @param activity Activity
     * @return true 需要
     */
    public static boolean isNeedImmersive(Activity activity) {
        return needImmersive(activity);
    }

    /**
     * 是否需要沉浸式适配
     *
     * @param context Context
     * @return true 需要
     */
    public static boolean isNeedImmersive(Context context) {
        return needImmersive(context);
    }

    /**
     * 获取沉浸式偏移高度
     *
     * @param activity Activity
     * @return 偏移高度
     */
    public static int getImmersiveOffset(Activity activity) {
        if (!isNeedImmersive(activity)) {
            return 0;
        }
        return getImmersiveOffset((Context) activity);
    }

    /**
     * 获取沉浸式偏移高度
     *
     * @param context Context
     * @return 偏移高度
     */
    public static int getImmersiveOffset(Context context) {
        if (!isNeedImmersive(context)) {
            return 0;
        }
        return getStatusBarHeight(context);
    }


    private static boolean needImmersive(Context context) {
        return context != null && isPhoneCanImmersive();
    }

    private static boolean isPhoneCanImmersive() {
        String brand = Build.BRAND;
        if (brand == null) {
            return false;
        }
        for (String unBrand : unImmersiveBrandArr) {
            if (brand.contains(unBrand)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 为沉浸式状态栏 设置背景颜色(必须是argb的颜色如：#FF000000)并且偏移指定距离
     * <p>
     * 首先为content设置padding
     * <p>
     * >= 5.0 直接设置背景颜色;
     * 其他 增加占位view，并为view设置背景
     *
     * @param activity Activity
     * @param color    int
     */
    public static void setStatusBarBgColorAndOffset(Activity activity, int color) {
        if (!isNeedImmersive(activity)) {
            return;
        }
        int height = getImmersiveOffset(activity);
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        if (rootView == null) {
            return;
        }
        //设置 paddingTop
        rootView.setPadding(0, height, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarBgColor(activity, color);
        } else {
            //增加占位状态栏
            StatusBarUnderAdr5View statusBarView = getCustomStatusBar(decorView);
            if (statusBarView == null) {
                statusBarView = new StatusBarUnderAdr5View(activity);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                decorView.addView(statusBarView, lp);
            }
            statusBarView.setBackgroundColor(color);
        }
    }

    /**
     * 移除沉浸式状态栏 背景颜色和偏移的距离
     * <p>
     * 首先为content移除padding
     * <p>
     * >= 5.0 直接设置背景透明;
     * 其他 移除占位view
     *
     * @param activity Activity
     */
    public static void removeStatusBarBgColorAndOffset(Activity activity) {
        if (!isNeedImmersive(activity)) {
            return;
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        if (rootView == null) {
            return;
        }
        //移除 paddingTop
        rootView.setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarBgColor(activity, Color.TRANSPARENT);
        } else {
            //移除占位状态栏
            StatusBarUnderAdr5View statusBarUnderAdr5View = getCustomStatusBar(decorView);
            if (statusBarUnderAdr5View != null) {
                decorView.removeView(statusBarUnderAdr5View);
            }
        }
    }

    /**
     * 返回设置过的StatusBar
     *
     * @param decorView
     * @return
     */
    private static StatusBarUnderAdr5View getCustomStatusBar(ViewGroup decorView) {
        int size = decorView.getChildCount();
        View childView;
        for (int i = 0; i < size; i++) {
            childView = decorView.getChildAt(i);
            if (childView != null && childView instanceof StatusBarUnderAdr5View) {
                return (StatusBarUnderAdr5View) childView;
            }
        }
        return null;
    }


    /**
     * 为状态栏 设置背景颜色 5.0 及以上生效;
     *
     * @param activity Activity
     * @param color    int
     */
    public static void setStatusBarBgColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //直接设置状态栏颜色
            activity.getWindow().setStatusBarColor(color);
        }
    }

    /**
     * 为Activity适配键盘弹出 需要setContentView后调用
     *
     * @param activity
     */
    public static void adaptShowKeyboard(Activity activity) {
        new KeyboardHelper(activity);
    }

    /**
     * 为沉浸式状态栏初始化window属性
     *
     * @param activity
     */
    public static void initWindowSetting(Activity activity) {
        Window window = activity.getWindow();
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 存在此属性会导致4.4版本虚拟按键挡住布局的问题
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * 自定义沉浸式状态栏初始化window属性
     *
     * @param activity
     */
    public static void initWindowSettingForCustomImmersive(Activity activity) {
        if (isNeedImmersive(activity)) {
            initWindowSetting(activity);
        } else {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    /**
     * 设置状态栏字体颜色
     * 只允许设置字体深色（需要6.0或以上版本支持）或者浅色
     *
     * @param activity Activity
     * @param isLight  是否浅色
     */
    public static void setStatusBarTextColor(Activity activity, boolean isLight) {
        if (!isLight && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 如果设置深色主题并且小于6.0直接返回
            return;
        }
        StatusBarTextColorHelper.setStatusBarTextColor(activity, isLight);
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private static class StatusBarUnderAdr5View extends View {

        public StatusBarUnderAdr5View(Context context) {
            super(context);
        }
        public StatusBarUnderAdr5View(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        public StatusBarUnderAdr5View(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
    }
}
