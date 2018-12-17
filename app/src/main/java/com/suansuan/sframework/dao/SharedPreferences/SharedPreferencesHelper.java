package com.suansuan.sframework.dao.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;


import java.util.Set;

/**
 * SharedPreferences#apply 会在 Activity onStop生命周期时阻塞主线程，所以采用将所有提交放到一个线程中进行统一的管理
 */
@SuppressWarnings("all")
public class SharedPreferencesHelper implements Storage{

    private static int mSize = -1;

    private static final String STORAGE_THREAD_NAME = "storageName";
    private static final String PREFERENCE_NAME = "suansuan_preference";
    private static final String DATA_PREFERENCE_NAME = "suansuan_data_preference";

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences dataSharedPreferences = null;

    private Context mContext;

    private static HandlerThread sStorageThread;
    private static Handler sStorageHandler;

    private SharedPreferencesHelper(Context context, String spName){
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences(
                TextUtils.isEmpty(spName) ? PREFERENCE_NAME : spName , Context.MODE_PRIVATE
        );
        mSize = sharedPreferences.getAll().size();
        dataSharedPreferences = context.getSharedPreferences(DATA_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesHelper getSharedPreferencesHelper (Context context) {
        return new SharedPreferencesHelper(context, null); // 使用默认的情况
    }

    public static SharedPreferencesHelper getSharedPreferencesHelper (Context context, String spName){
        return new SharedPreferencesHelper(context, spName);
    }

    @Override
    public void putString(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return ;
        }
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        apply(edit);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        apply(editor);
    }

    @Override
    public void putInt(String key, int value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        apply(editor);
    }

    @Override
    public void putLong(String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        apply(editor);
    }

    @Override
    public void putFloat(String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        apply(editor);
    }

    @Override
    public void putStringSet(String key, Set<String> value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        apply(editor);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return false;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return 0;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return null;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return null;
    }

    @Override
    public String getDataString(String key, String defaultValue) {
        return null;
    }

    @Override
    public void putDataString(String key, String value) {

    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public void append(String key, String value) {

    }

    @Override
    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        apply(editor);
    }

    @Override
    public void popToFileAndClean(boolean forcePop, String filePath) {

    }

    @Override
    public void clean() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear().commit();
        mSize = 0 ;
    }

    private void apply(final SharedPreferences.Editor editor) {
        getStorageHandler().post(new Runnable() {
            @Override
            public void run() {
                editor.commit();
            }
        });
    }

    private HandlerThread getStorageHandlerThread(){
        if(sStorageThread == null){
            sStorageThread = new HandlerThread(STORAGE_THREAD_NAME);
            sStorageThread.start();
        }
        return sStorageThread;
    }

    private Handler getStorageHandler() {
        if(sStorageHandler == null){
            synchronized (SharedPreferencesHelper.class) {
                if(sStorageHandler == null){
                    sStorageHandler = new Handler(getStorageHandlerThread().getLooper());
                }
            }
        }
        return sStorageHandler;
    }
}
