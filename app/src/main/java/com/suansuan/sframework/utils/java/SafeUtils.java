package com.suansuan.sframework.utils.java;

import android.annotation.TargetApi;
import android.util.Base64;

import java.security.MessageDigest;

@SuppressWarnings("all")
public class SafeUtils {

    /**
     * 对字符串进行加密
     *
     * @param originStr 需要加密的String
     * @return 加密后的String
     */
    @TargetApi(8)
    public static String ea(String originStr) {
        try {
            //1.String2byte[]；2.加密；3.Base64
            byte[] originByte = originStr.getBytes("UTF-8");
            byte[] encipheredByte = new byte[originByte.length + 1];
            encipheredByte[0] = (byte) 7;
            System.arraycopy(originByte, 0, encipheredByte, 1, originByte.length);
            // TODO 应该调用so文件，目前未实现   加密
//            originByte = SafeUtils.ea(encipheredByte);

            return Base64.encodeToString(originByte, Base64.NO_WRAP);
        } catch (Throwable throwable) {
//            log.error("ea str failed : " + throwable);
            return originStr;
        }

    }

    /**
     * 对字符串进行解密
     */
    @TargetApi(8)
    public static String da(String encipheredStr) {
        try {
            //1.反Base64；2.解密；3.byte[]2String
            byte[] encipheredByte = Base64.decode(encipheredStr, Base64.NO_WRAP);
            // TODO 应该调用so文件，目前未实现   加密
//            encipheredByte = da(encipheredByte);
            byte[] originByte = new byte[encipheredByte.length - 1];
            System.arraycopy(encipheredByte, 1, originByte, 0, originByte.length);

            return new String(originByte, "UTF-8");
        } catch (Throwable throwable) {
//            log.error("ea str failed : " + throwable);
            return encipheredStr;
        }
    }

    /**
     * 字符串 MD5 加密
     * @param string 要加密的字符串
     * @return 加密以后的字符串
     */
    public static String stringToMD5(String string) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10)
                    hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Throwable e) {}
        return "";
    }

    // 通过JNI 回掉到C层。
    public static native byte[] ea(byte[] input);
    public static native byte[] da(byte[] input);
}
