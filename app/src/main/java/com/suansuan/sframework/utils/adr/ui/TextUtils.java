package com.suansuan.sframework.utils.adr.ui;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.suansuan.sframework.utils.java.CheckUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Android 当中字体的相关Utils
 */
@SuppressWarnings("all")
public class TextUtils {

    /**
     * 将字符串加粗
     *
     * @param sUnbold 不加粗的字符串，可以为空
     * @param bold    加粗的字符串，不可以为空
     * @param eUnbold 不加粗的字符串，可以为空
     */
    public static CharSequence toBold(String sUnbold, String bold, String eUnbold) {
        if (CheckUtils.isEmpty(bold)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int s = CheckUtils.isEmpty(sUnbold) ? 0 : builder.append(sUnbold).length();
        int m = CheckUtils.isEmpty(bold) ? 0 : builder.append(bold).length();
        int e = CheckUtils.isEmpty(eUnbold) ? 0 : builder.append(eUnbold).length();
        SpannableString boldSS = new SpannableString(builder.toString());
        boldSS.setSpan(new StyleSpan(Typeface.BOLD), s, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return boldSS;
    }

    /**
     * 将字符串加粗
     *
     * @param sUncolor 不加粗的字符串，可以为空
     * @param mColor   加粗的字符串，不可以为空
     * @param eUncolor 不加粗的字符串，可以为空
     */

    public static CharSequence toColor(String sUncolor, String mColor, String eUncolor, int color) {
        if (CheckUtils.isEmpty(mColor)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int s = CheckUtils.isEmpty(sUncolor) ? 0 : builder.append(sUncolor).length();
        int m = CheckUtils.isEmpty(mColor) ? 0 : builder.append(mColor).length();
        int e = CheckUtils.isEmpty(eUncolor) ? 0 : builder.append(eUncolor).length();
        SpannableString coloredSS = new SpannableString(builder.toString());
        coloredSS.setSpan(new ForegroundColorSpan(color), s, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return coloredSS;
    }

    /**
     * 把字符串中的数字修改颜色，注意赋值给textview的时候直接赋值，不要用toString，否则格式会丢失
     *
     * @param sColor 字符串
     * @param color  要修改的颜色值
     */
    public static CharSequence colorDigitsInString(String sColor, int color) {
        if (CheckUtils.isEmpty(sColor)) {
            return null;
        }
        Pattern p = Pattern.compile("\\d+"); //匹配数字
        Matcher m = p.matcher(sColor);
        int start = 0;
        Map<Integer, Integer> indice = new HashMap<Integer, Integer>(); //map里面记录数字部分的开始和结束index
        while (m.find(start)) {
            int index = sColor.indexOf(m.group(0), start);
            start = index + m.group(0).length();
            indice.put(index, start);
        }
        SpannableString coloredSS = new SpannableString(sColor.toString());
        for (Integer indexStart : indice.keySet()) {
            Integer indexEnd = indice.get(indexStart);
            coloredSS.setSpan(new ForegroundColorSpan(color), indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return coloredSS;
    }

    public static CharSequence toBold(String bold) {
        return toBold(null, bold, null);
    }

    public static CharSequence toSize(String sSize, int size) {
        return toSize(null, sSize, null, size);
    }

    public static CharSequence toSize(String sUnsize, String sSize, String eUnsize, int size, boolean dip) {
        if (CheckUtils.isEmpty(sSize)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int s = CheckUtils.isEmpty(sUnsize) ? 0 : builder.append(sUnsize).length();
        int m = CheckUtils.isEmpty(sSize) ? 0 : builder.append(sSize).length();
        int e = CheckUtils.isEmpty(eUnsize) ? 0 : builder.append(eUnsize).length();
        SpannableString sizeSS = new SpannableString(builder.toString());
        sizeSS.setSpan(new AbsoluteSizeSpan(size, dip), s, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sizeSS;
    }

    public static CharSequence toSize(String sUnsize, String sSize, String eUnsize, int size) {
        return toSize(sUnsize, sSize, eUnsize, size, true);
    }

    public static CharSequence joinSpan(CharSequence... sequences) {
        if (CheckUtils.isEmpty(sequences)) {
            return null;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (CharSequence cs : sequences) {
            builder.append(cs);
        }
        return builder.subSequence(0, builder.length());
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
}
