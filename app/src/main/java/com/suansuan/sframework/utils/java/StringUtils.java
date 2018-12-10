package com.suansuan.sframework.utils.java;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

@SuppressWarnings("all")
public class StringUtils {

    public static final String ACTIVITY_METRIC_PREFIX = "Mobile/Activity/Name/";
    public static final String ACTIVITY_BACKGROUND_METRIC_PREFIX = "Mobile/Activity/Background/Name/";
    public static final String ACTIVITY_DISPLAY_NAME_PREFIX = "Display ";
    private static final Random random = new Random();

    /**
     * 将字符串进行压缩　gzip
     *
     * @param content     压缩的字符串
     * @param outFileName 压缩文件的输出位置和名称
     */
    public static void doCompressString(String content, String outFileName) {
        InputStream in = null;
        GZIPOutputStream out = null;
        try {
            in = new ByteArrayInputStream(content.getBytes());
            out = new GZIPOutputStream(new FileOutputStream(outFileName));
            //读取字节流到压缩流
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.safeClose(in);
            IOUtils.safeClose(out);
        }
    }

    public static Random getRandom() {
        return random;
    }

    public static String formatActivityMetricName(String name) {
        return ACTIVITY_METRIC_PREFIX + name;
    }

    public static String formatActivityBackgroundMetricName(String name) {
        return ACTIVITY_BACKGROUND_METRIC_PREFIX + name;
    }

    public static String formatActivityDisplayName(String name) {
        return ACTIVITY_DISPLAY_NAME_PREFIX + name;
    }

    public static String null2String(String str) {
        return str == null ? "" : str;
    }
}
