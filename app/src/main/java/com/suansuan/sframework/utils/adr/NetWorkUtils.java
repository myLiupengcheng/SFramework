package com.suansuan.sframework.utils.adr;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.text.MessageFormat;

@SuppressWarnings("all")
public class NetWorkUtils {

    private static final String ANDROID = "Android";
    private static final String WIFI = "wifi";
    private static final String UNKNOWN = "unknown";
    private static final String UNCONNECT = "unconnect";

    private static String imei = null;
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");


    /**
     * 获取设备IMEI
     * @param context Android 上下文环境
     * @return 设备IMEI
     */
    public static String getIMEI(Context context) {
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Throwable e) {

        }
        return imei;
    }


    /**
     * 获取设备APN的名字
     * @param context Android 上下文环境
     * @return 设备APN名字
     */
    public static String getApnName(Context context) {
        String apnName = "";
        try {
            Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI, new String[]{"_id", "apn", "type"}, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int counts = cursor.getCount();
                if (counts != 0) {
                    if (!cursor.isAfterLast()) {
                        apnName = cursor.getString(cursor.getColumnIndex("apn"));
                    }
                }
                cursor.close();
            } else {
                // 适配中国电信定制机,如海信EG968,上面方式获取的cursor为空，所以换种方式
                cursor = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    apnName = cursor.getString(cursor.getColumnIndex("user"));
                    cursor.close();
                }
            }
        } catch (Exception e) {
            try {
                ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo ni = conManager.getActiveNetworkInfo();
                apnName = ni.getExtraInfo();
            } catch (Exception e1) {
                apnName = "";
            }
        }
        return apnName;
    }

    /**
     * 获取设备运营商名字
     * @param context Android 上下文环境
     * @return 设备运营商名字
     */
    public static String getSimOperator(Context context) {
        String operator = "";
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            operator = manager.getSimOperator();
        } catch (Throwable e) {

        }
        return operator;
    }

    /**
     * 判断当前手机是否联网
     * @param context Android 上下文环境
     * @return
     *          true：手机联网状态
     *          false：手机断网状态
     */
    public static boolean isNetworkConnected(Context context) {
        if(context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if(mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取网络类型
     * @param context Android 上下文环境
     * @return 网络类型
     */
    public static String carrierNameFromContext(Context context) {
        NetworkInfo networkInfo;
        try {
            networkInfo = getNetworkInfo(context);
        } catch (SecurityException var3) {
            return UNKNOWN;
        }
        if(!isConnected(networkInfo)) {
            return UNCONNECT;
        } else if(isWan(networkInfo)) {
            return carrierNameFromTelephonyManager(context);
        } else if(isWifi(networkInfo)) {
            return WIFI;
        } else {
            Log.i("suansuan",MessageFormat.format("Unknown network type: {0} [{1}]", new Object[]{networkInfo.getTypeName(), Integer.valueOf(networkInfo.getType())}));
            return UNKNOWN;
        }
    }

    private static boolean isWan(NetworkInfo networkInfo) {
        switch(networkInfo.getType()) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            case 1:
            default:
                return false;
        }
    }

    private static boolean isWifi(NetworkInfo networkInfo) {
        switch(networkInfo.getType()) {
            case 1:
            case 6:
            case 7:
            case 9:
                return true;
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
            default:
                return false;
        }
    }

    private static boolean isConnected(NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isConnected();
    }

    private static NetworkInfo getNetworkInfo(Context context) throws SecurityException {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            return connectivityManager.getActiveNetworkInfo();
        } catch (SecurityException var3) {
            Log.i("suansuan","Cannot determine network state. Enable android.permission.ACCESS_NETWORK_STATE in your manifest.");
            throw var3;
        }
    }

    private static String carrierNameFromTelephonyManager(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = telephonyManager.getSimOperator();
        if(networkOperator == null || networkOperator.trim().isEmpty()){
            networkOperator = "unknown";
        }
        boolean smellsLikeAnEmulator = Build.PRODUCT.equals("google_sdk") || Build.PRODUCT.equals("sdk") || Build.PRODUCT.equals("sdk_x86") || Build.FINGERPRINT.startsWith("generic");
        return networkOperator.equals(ANDROID) && smellsLikeAnEmulator ? WIFI : networkOperator;
    }
}
