package com.suansuan.sframework.dao.SharedPreferences;

import java.util.Set;

public interface Storage {

    void putString(String key, String value);
    void putBoolean(String key, boolean value);
    void putInt(String key, int value);
    void putLong(String key, long value);
    void putFloat(String key, long value);
    void putStringSet(String key, Set<String> value);

    boolean getBoolean(String key, boolean defaultValue);
    int getInt(String key, int defaultValue);
    long getLong(String key, long defaultValue);
    String getString(String key, String defaultValue);
    Set<String> getStringSet(String key, Set<String> defaultValue);

    String getDataString(String key, String defaultValue);
    void putDataString(String key, String value);


    boolean contains(String key);

    void append(final String key, final String value);
    void remove(String key);
    void popToFileAndClean(final boolean forcePop, String filePath);
    void clean();
}
