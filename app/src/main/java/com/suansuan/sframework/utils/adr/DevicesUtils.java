package com.suansuan.sframework.utils.adr;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;

/**
 * 获取设备相关的信息
 */
@SuppressWarnings("all")
public class DevicesUtils {

    private static final String UNKNOWN = "unknown";
    private static final String SN_CLASS = "android.os.SystemProperties";
    private static final String SN_CLASS_METHOD = "get";
    private static final String SN_CLASS_DECLARED_1 = "ro.serialno";
    private static final String SN_CLASS_DECLARED_2 = "gsm.device.sn";
    private static final String SN_CLASS_DECLARED_3 = "ril.serialnumber";
    private static final String COMMAND_MAC = "cat /sys/class/net/wlan0/address";

    private static String macSerial = "";
    private static String sn = UNKNOWN;

    /**
     * 获取设备唯一的mac地址
     * @return 设备Mac地址
     */
    public static String getMac() {
        if(!TextUtils.isEmpty(macSerial)){
            return macSerial;
        }
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec(COMMAND_MAC);
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Throwable e) {

        }
        return macSerial;
    }


    /**
     * Android设备硬件序列号（SN、串号）的序列号
     *  frameworks/base/core/java/android/os/Build.java:
     * @return Android设备硬件序列号（SN、串号）
     */
    public static String getSN() {
        if(!UNKNOWN.equals(sn)){
            return sn;
        }
        try {
            if (Build.VERSION.SDK_INT >= 9) {
                sn = new Object() {

                    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
                    public String getSerial() {
                        return Build.SERIAL;
                    }
                }.getSerial();
            }
            if (UNKNOWN.equals(sn)) {
                try {
                    Class<?> c = Class.forName(SN_CLASS);
                    Method m = c.getDeclaredMethod(SN_CLASS_METHOD, String.class, String.class);
                    sn = (String) m.invoke(null, SN_CLASS_DECLARED_1, UNKNOWN);
                    if (UNKNOWN.equals(sn)) {
                        sn = (String) m.invoke(null, SN_CLASS_DECLARED_2, UNKNOWN);
                    }
                    if (UNKNOWN.equals(sn)) {
                        sn = (String) m.invoke(null, SN_CLASS_DECLARED_3, UNKNOWN);
                    }
                } catch (Exception e) {}
            }
            if (UNKNOWN.equals(sn)) {
                return "";
            }
        } catch (Throwable e) {

        }
        return sn;
    }
}
