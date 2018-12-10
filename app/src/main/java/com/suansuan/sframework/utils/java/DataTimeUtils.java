package com.suansuan.sframework.utils.java;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 日期时间工具类，统一了所有的日期显示格式
 */
@SuppressWarnings("all")
public class DataTimeUtils {

    public static final int DATETIME_FIELD_REFERSH = 10; // 刷新时间(分钟),

    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = ONE_SECOND * 60L;
    public static final long ONE_HOUR = ONE_MINUTE * 60L;
    public static final long ONE_DAY = ONE_HOUR * 24L;

    public static final String MM_Yue_dd_Ri = "MM月dd日";         // 下面的pattern在print和parse时都可以使用
    public static final String M_Yue_d_Ri = "M月d日";
    public static final String d_Ri = "d日";
    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyy_MM = "yyyy-MM";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String HH_mm = "HH:mm";
    public static final String yyyy_Nian_MM_Yue_dd_Ri = "yyyy年MM月dd日";
    public static final String yyyy_Nian_MM_Yue = "yyyy年MM月";
    public static final String MM_yy = "MM/yy";
    public static final String dd_MM = "dd/MM";
    public static final String MM_dd = "MM-dd";
    public static final String HH_mm_ss = "HH:mm:ss";               // 下面的pattern是print时用，parse时不应使用（只有时间，没有日期）
    public static final String KEY_TSLGAPM = "chaos.liu.tslgapm";

    private static final String[] PATTERNS = {yyyy_MM_dd_HH_mm_ss, yyyy_MM_dd_HH_mm, yyyy_MM_dd, yyyyMMdd};

    public static void cleanCalendarTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 获得指定日期表示格式转换成Calendar的格式
     *
     * @param src
     * @param fallback 若无法转换，返回一个默认值
     * @return
     */
    public static <T> Calendar getCalendar(T src, Calendar fallback) {
        if (src != null) {
            try {
                return getCalendar(src);
            } catch (Exception e) {
                // do nothing
            }
        }
        return (Calendar) fallback.clone();
    }

    /**
     * 获得日期类型
     *
     * @param src 任何可以表示时间的类型，目前支持Calendar,Date,long,String
     * @return Calendar类型表示的时间
     * @throws IllegalArgumentException
     */
    public static <T> Calendar getCalendar(T src) {
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        if (src == null) {
            return null;
        } else if (src instanceof Calendar) {
            calendar.setTimeInMillis(((Calendar) src).getTimeInMillis());
        } else if (src instanceof Date) {
            calendar.setTime((Date) src);
        } else if (src instanceof Long) {
            calendar.setTimeInMillis((Long) src);
        } else if (src instanceof String) {
            String nSrc = (String) src;
            if (CheckUtils.isEmpty(nSrc)) {
                return null;
            }
            try {
                // 直接匹配的时候不能匹配到月份或日期不是2位数的情况
                if (Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日").matcher(nSrc).find()) {
                    nSrc = fixDateString(nSrc);
                    return getCalendarByPattern(nSrc, yyyy_MM_dd);
                }
                return getCalendarByPatterns(nSrc, PATTERNS);
            } catch (Exception e) {
                try {
                    calendar.setTimeInMillis(Long.valueOf(nSrc));
                } catch (NumberFormatException e1) {
                    throw new IllegalArgumentException(e1);
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
        return calendar;
    }

    /**
     * YYYY年MM月DD日 --> YYYY-MM-DD
     */
    private static String fixDateString(String date) {
        if (CheckUtils.isEmpty(date)) {
            return date;
        }

        String[] dateArray = date.split("[年月日]");
        if (dateArray.length == 1) {
            dateArray = date.split("-");
        }
        for (int i = 0; i < 3; i++) {
            if (dateArray[i].length() == 1) {
                dateArray[i] = "0" + dateArray[i];
            }
        }
        return dateArray[0] + "-" + dateArray[1] + "-" + dateArray[2];
    }

    /**
     * 匹配pattern获得时间，若无法解析抛出异常
     *
     * @param dateTimeStr
     * @param patternStr
     * @return
     * @throws IllegalArgumentException
     */
    public static Calendar getCalendarByPattern(String dateTimeStr, String patternStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(patternStr, Locale.US);
            sdf.setLenient(false);
            Date d = sdf.parse(dateTimeStr);
            Calendar c = Calendar.getInstance();
            c.setLenient(false);
            c.setTimeInMillis(d.getTime());
            return c;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 匹配pattern数组中的所有pattern解析时间格式，若没有可以解析的方式则抛出异常
     *
     * @param dateTimeStr
     * @param patternStr
     * @return
     * @throws IllegalArgumentException
     */
    public static Calendar getCalendarByPatterns(String dateTimeStr, String[] patternStr) {
        for (String string : patternStr) {
            try {
                return getCalendarByPattern(dateTimeStr, string);
            } catch (Exception e) {
                // do nothing
            }
        }

        throw new IllegalArgumentException();
    }

    /**
     * 是否有服务器时间
     */
    public static boolean hasServerTime;
    /**
     * 本地时间和服务器时间的间隔 time server local gap millis
     */
    public static long tslgapm;
    /**
     * 本地时间和服务器时间的间隔 time server string
     */
    public static String tss;

    /**
     * 获取与服务器时间矫正过的当前时间
     */
    public static Calendar getCurrentDateTime() {
        Calendar now = Calendar.getInstance();
        now.setLenient(false);
        if (hasServerTime) {
            now.setTimeInMillis(now.getTimeInMillis() + tslgapm);
        }
        return now;
    }

    /**
     * login时server的日期
     *
     * @return
     */
    public static Calendar getLoginServerDate() {
        return getCalendar(tss);
    }

    /**
     * 获得基准日期增加间隔天
     */
    public static Calendar getDateAdd(Calendar start, int interval) {
        if (start == null) {
            return null;
        }
        Calendar c = (Calendar) start.clone();
        c.add(Calendar.DATE, interval);
        return c;
    }

    /**
     * 获得时间间隔
     *
     * @param from
     * @param to
     * @return
     */
    public static long getIntervalTimes(Calendar from, Calendar to, long unit) {
        if (from == null || to == null) {
            return 0;
        }
        return Math.abs(from.getTimeInMillis() - to.getTimeInMillis()) / unit;
    }

    /**
     * 获得日期间隔 忽略小时
     *
     * @param startdate
     * @param enddate
     * @return
     */

    public static int getIntervalDays(String startdate, String enddate, String pattern) {
        int betweenDays = 0;
        if (startdate == null || enddate == null) {
            return betweenDays;
        }

        Calendar d1 = getCalendarByPattern(startdate, pattern);
        Calendar d2 = getCalendarByPattern(enddate, pattern);

        return getIntervalDays(d1, d2);
    }

    public static <T> int getIntervalDays(T from, T to) {
        Calendar startdate = getCalendar(from);
        Calendar enddate = getCalendar(to);
        cleanCalendarTime(startdate);
        cleanCalendarTime(enddate);
        return (int) getIntervalTimes(startdate, enddate, ONE_DAY);
    }

    private static String[] weekdays = {"", "周日", "周一", "周二", "周三", "周四", "周五", "周六", };
    private static String[] weekdays1 = {"", "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", };

    /**
     * calendar 周一～周日
     */
    public static String getWeekDayFromCalendar(Calendar cal) {
        if (cal == null) {
            throw new IllegalArgumentException();
        }
        return weekdays[cal.get(Calendar.DAY_OF_WEEK)];
    }

    /**
     * calendar 星期日～星期六
     */
    public static String getWeekDayFromCalendar1(Calendar cal) {
        if (cal == null) {
            throw new IllegalArgumentException();
        }
        return weekdays1[cal.get(Calendar.DAY_OF_WEEK)];
    }

    /**
     * 判断是否是闰年 这个方法不要改动！
     *
     * @param date(2009-10-13 || 2009年10月13日 || 2009)
     * @return true 是 false 不是
     * @author jie.cui
     */
    public static boolean isLeapyear(String date) {
        Calendar calendar = getCalendar(date);
        if (calendar != null) {
            int year = calendar.get(Calendar.YEAR);
            return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
        }
        return false;
    }

    // 是否到刷新时间
    public static boolean isRefersh(long beforeTime) {
        return isRefersh(DATETIME_FIELD_REFERSH * 1000 * 60, beforeTime);
    }

    // 是否到刷新时间
    public static boolean isRefersh(long gap, long beforeTime) {
        return new Date().getTime() - beforeTime >= gap;
    }

    public static String printCalendarByPattern(Calendar c, String patternStr) {
        if (null == c || null == patternStr) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(patternStr, Locale.US);
        sdf.setLenient(false);
        return sdf.format(c.getTime());
    }

    /**
     * 只通过年月日比较两个Calendar
     *
     * @return <code>c1 < c2 = -1 ; c1 > c2 = 1 ; c1 == c2 = 0</code>
     */
    public static int compareCalendarIgnoreTime(Calendar c1, Calendar c2) {
        if (c1.get(Calendar.YEAR) > c2.get(Calendar.YEAR)) {
            return 1;
        } else if (c1.get(Calendar.YEAR) < c2.get(Calendar.YEAR)) {
            return -1;
        } else {
            if (c1.get(Calendar.MONTH) > c2.get(Calendar.MONTH)) {
                return 1;
            } else if (c1.get(Calendar.MONTH) < c2.get(Calendar.MONTH)) {
                return -1;
            } else {
                if (c1.get(Calendar.DAY_OF_MONTH) > c2.get(Calendar.DAY_OF_MONTH)) {
                    return 1;
                } else if (c1.get(Calendar.DAY_OF_MONTH) < c2.get(Calendar.DAY_OF_MONTH)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    public static void setTimeWithHHmm(Calendar src, String HH_mm) {
        if (CheckUtils.isEmpty(HH_mm) || null == src) {
            return;
        }
        String s[] = HH_mm.split(":");
        if (s.length != 2) {
            return;
        }
        try {
            cleanCalendarTime(src);
            src.set(Calendar.HOUR_OF_DAY, Integer.valueOf(s[0]));
            src.set(Calendar.MINUTE, Integer.valueOf(s[1]));
        } catch (NumberFormatException e) {
        }

    }
}
