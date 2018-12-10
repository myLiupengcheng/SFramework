package com.suansuan.sframework.utils.adr.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

import com.suansuan.sframework.utils.java.CheckUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Android ui 相关View的工具类
 */
@SuppressWarnings("all")
public class ViewUtils {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * copy from {@link android.view.View#generateViewId()}
     * <br> Added in API level 17,
     * <br> <em>now API level 1.</em>
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range
            // under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * 生成一个ID，让系统误以为是R中的id
     * <br> 高位为1<br>
     * 如果为View设置ID，请使用 {@link ViewUtils#generateViewId()},此方法仅用来{@link
     * android.view.View#setTag(int, Object)}<br>
     */
    public static int fakeGenId() {
        int realId = generateViewId();
        int fakeId = realId | 0x10000000;
        return fakeId;
    }

    /**
     * 根据R中的ID，生成一个与之相关绝不重复的Id
     * <br> 高位为2
     * 若Id为0,则会生成新的Id
     *
     * @param id
     * @author chaos.liu
     * @since 2013年11月26日上午11:49:42
     */
    public static int unionGenId(int id) {
        if (id == 0) {
            throw new IllegalArgumentException("Id NOT ALLOW 0 !");
        }
        int realId = id & 0x0FFFFFFF;
        int fakeId = realId | 0x20000000;
        return fakeId;
    }


    /**
     * 当CharSequence为null时，View设置为gone<br> 当CharSequence不为null时,View设置为visible.
     *
     * @param v  不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param cs 当cs为null时，View设置为gone, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成gone
     */
    public static boolean setOrGone(View v, CharSequence cs) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Text, true, View.GONE, cs);
    }

    /**
     * 当CharSequence为null时，View设置为invisible<br> 当CharSequence不为null时,View设置为visible.
     *
     * @param v  不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param cs 当cs为null时，View设置为invisible, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成invisible
     */
    public static boolean setOrHide(View v, CharSequence cs) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Text, true, View.INVISIBLE, cs);
    }

    /**
     * /** 当Drawable为null时，View设置为gone<br> 当Drawable不为null时,View设置为visible.
     *
     * @param v        不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param drawable 当drawable为null时，View设置为gone, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成gone
     */
    public static boolean setOrGone(View v, Drawable drawable) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Background, true, View.GONE, drawable);
    }

    /**
     * 当Drawable为null时，View设置为invisible<br> 当Drawable不为null时,View设置为visible.
     *
     * @param v        不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param drawable 当drawable为null时，View设置为invisible, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成invisible
     */
    public static boolean setOrHide(View v, Drawable drawable) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Background, true, View.INVISIBLE, drawable);
    }

    /**
     * /** 当CharSequence为null时，View设置为gone<br> 当CharSequence不为null时,View设置为visible.
     *
     * @param v  不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param cs 当cs为null时，View设置为gone, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成gone
     */
    public static boolean setOrGone(View v, CharSequence... cs) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Text, true, View.GONE, (Object[]) cs);
    }

    /**
     * 当CharSequence为null时，View设置为invisible<br> 当CharSequence不为null时,View设置为visible.
     *
     * @param v  不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param cs 当cs为null时，View设置为invisible, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成invisible
     */
    public static boolean setOrHide(View v, CharSequence... cs) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Text, true, View.INVISIBLE, (Object[]) cs);
    }

    /**
     * 当condition为true时，View设置为visible<br> 当condition为false时,View设置为gone.
     *
     * @param v         不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param condition 为false的时候会将View设置为gone, otherwise.
     * @return false表示View被设置成gone , otherwise.
     */
    public static boolean setOrGone(View v, boolean condition) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.NotCare, condition, View.GONE);
    }

    /**
     * 当condition为true时，View设置为visible<br> 当condition为false时,View设置为invisible.
     *
     * @param v         不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param condition 为false的时候会将View设置为invisible, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成invisible
     */
    public static boolean setOrHide(View v, boolean condition) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.NotCare, condition, View.INVISIBLE);
    }

    /**
     * 当condition为true时，将CharSequence赋给View,并将View设置为visible<br> 当condition为false或CharSequence为null时，将View设置为gone.
     *
     * @param v         不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param condition 为false的时候会无视参数，将View设置为gone.
     * @param cs        对View设置的内容
     * @return true 表示对View操作（赋值）成功，false表示View被设置成gone
     */
    public static boolean setOrGone(View v, boolean condition, CharSequence cs) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Text, condition, View.GONE, cs);
    }

    /**
     * 当condition为true时，将CharSequence赋给View,并将View设置为visible<br> 当condition为false或CharSequence为null时，将View设置为invisible.
     *
     * @param v         不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param condition 为false的时候会无视参数，将View设置为invisible.
     * @param cs        对View设置的内容
     * @return true 表示对View操作（赋值）成功，false表示View被设置成invisible
     */
    public static boolean setOrHide(View v, boolean condition, CharSequence cs) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Text, condition, View.INVISIBLE, cs);
    }

    /**
     * 当Bitmap为null时，View设置为gone<br> 当Bitmap不为null时,View设置为visible.
     *
     * @param v   不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param res 当res为null时，View设置为gone, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成gone
     */
    public static boolean setOrGone(View v, Bitmap res) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Src, true, View.GONE, res);
    }

    /**
     * 当Bitmap为null时，View设置为hide<br> 当Bitmap不为null时,View设置为invisible.
     *
     * @param v   不能为空(debug模式下会抛异常，非debug模式会进行fake)
     * @param res 当res为null时，View设置为gone, otherwise.
     * @return true 表示对View操作（赋值）成功，false表示View被设置成gone
     */
    public static boolean setOrHide(View v, Bitmap res) {
        return ViewSetter.getSetting(v).setOr(ViewSetter.Method.Src, true, View.INVISIBLE, res);
    }

    public static CharSequence joinAllowedNull(Object... str) {
        if (CheckUtils.isEmpty(str)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        for (Object o : str) {
            if (CheckUtils.isEmpty(o)) {
                continue;
            }
            sb.append(o.toString());
        }

        if (CheckUtils.isEmpty(sb)) {
            return null;
        } else {
            return sb.toString();
        }
    }

    /**
     * 拼接字符串，任何一个字符串为空时，将返回空.
     *
     * @param str
     */
    public static CharSequence joinNotAllowedNull(Object... str) {
        if (CheckUtils.isContainsEmpty(str)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : str) {
            sb.append(o.toString());
        }
        return sb.toString();
    }

    /**
     * 为色块做一点颜色点击的效果
     *
     * @param context
     * @param color
     * @author Chaos
     * @since 2014年1月5日下午12:01:51
     */
    public static StateListDrawable makeColorMask(Context context, int color) {
        return makeColorMask(context, color, 0x09000000);
    }

    public static StateListDrawable makeColorMask(Context context, int color, int argb) {
        // init
        ColorDrawable unstate = new ColorDrawable(color);
        Bitmap pressed = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(pressed);
        canvas.drawColor(color);
        canvas.drawColor(0x09000000);

        StateListDrawable stateDrawable = new StateListDrawable();
        int statePressed = android.R.attr.state_pressed;
        stateDrawable.addState(new int[]{-statePressed}, unstate);
        stateDrawable.addState(new int[]{statePressed}, new BitmapDrawable(pressed));
        return stateDrawable;
    }

    public static StateListDrawable makeColorMask(Context context, Bitmap bi) {
        return makeColorMask(context, bi, 0x09000000);
    }

    public static StateListDrawable makeColorMask(Context context, Bitmap bi, int argb) {
        // init
        Bitmap unstate = bi.copy(Bitmap.Config.ARGB_8888, false);
        Bitmap pressed = bi.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(pressed);
        canvas.drawColor(argb);

        StateListDrawable stateDrawable = new StateListDrawable();
        int statePressed = android.R.attr.state_pressed;
        stateDrawable.addState(new int[]{-statePressed}, new BitmapDrawable(unstate));
        stateDrawable.addState(new int[]{statePressed}, new BitmapDrawable(pressed));
        return stateDrawable;
    }


    public static Drawable scaleByDensity(Context context, Drawable drawable) {
        int width = (int) (drawable.getIntrinsicWidth() * context.getResources().getDisplayMetrics().density);
        int height = (int) (drawable.getIntrinsicHeight() * context.getResources().getDisplayMetrics().density);
        drawable.setBounds(0, 0, width, height);
        return drawable;
    }

    public static Drawable scaleByDensity(Context context, Bitmap bitmap) {
        return scaleByDensity(context, new BitmapDrawable(bitmap));
    }
}
