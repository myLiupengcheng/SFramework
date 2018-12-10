package com.suansuan.sframework.utils.adr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;

import com.suansuan.sframework.FrameworkConstantPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装SharedPreferences 的存储工具类
 *
 * 优点如下 ：
 *      1.put之后确保能够立即get到 -- put操作立即存储到memory；remove和commit衔接无缝隙
 *      2.确保getAll相关的准确性
 *      3.clean操作无死角生效
 */
@SuppressWarnings("all")
public class SStorageUtils {

    private Map<String, Object> mMemoryCacheData;
    private SharedPreferences sp;

    private static HandlerThread sStorageHandlerThread;
    private static Handler sStorageHandler;

    private SStorageUtils(Context context, String name){
        this.sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        mMemoryCacheData = new HashMap<>();
    }

    public static SStorageUtils newInstance(Context context, String name){
        return new SStorageUtils(context, name);
    }


    private Handler getStorageHandler(){
        if(sStorageHandler == null){
            synchronized (SStorageUtils.class){
                if(sStorageHandler == null){
                    sStorageHandler = new Handler(getStorageHandlerThread().getLooper());
                }
            }
        }
        return sStorageHandler;
    }

    /**
     * 获取用于Framework相关使用的StorageThread
     * @return StorageThread
     */
    private HandlerThread getStorageHandlerThread(){
        if(sStorageHandlerThread == null){
            sStorageHandlerThread = new HandlerThread(FrameworkConstantPool.STORAGE_THREAD_NAME);
            sStorageHandlerThread.start();
        }
        return sStorageHandlerThread;
    }

    // 1.确保put之后可以立即get到；
    // 2.确保getAll准确性不受put影响；
    // 3.确保clean操作的完整性
    public void putString(String key, String value) {
        synchronized (SStorageUtils.this) {
            putData2MemoryCache(key, value);
            try {
                SharedPreferences.Editor editor = sp.edit().putString(key, value);
                submitEditor(editor, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getString(String key, String defVal) {
        String result;
        synchronized (SStorageUtils.this) {//1.确保取出的数据准确性
            //先从内存缓存中取数据
            Object tempData = getDataFromMemoryCache(key);
            if (tempData != null) {
                return (String) tempData;
            }
            result = sp.getString(key, defVal);
        }
        return result;
    }

    public void clean() {
        synchronized (SStorageUtils.this) {//1.确保clean操作的完整性
            //1.清除过渡缓存
            mMemoryCacheData.clear();
            //2.清除所有待提交的数据
            getStorageHandler().removeCallbacksAndMessages(null);
            //3.清除sp
            sp.edit().clear().apply();
        }
    }


    /**
     * 返回当前SP item数量
     */
    @SuppressWarnings("unchecked")
    public int getCount() {
        Map<String, Object> tempCacheData;
        synchronized (SStorageUtils.this) {
            tempCacheData = (Map<String, Object>) sp.getAll();
            if (mMemoryCacheData != null && !mMemoryCacheData.isEmpty()) {
                tempCacheData.putAll(mMemoryCacheData);
            }
        }
        return tempCacheData.size();
    }

    /**
     * 获取所有的数据
     */
    public List<String> getKeys() {
        Map<String, Object> tempCacheData;
        synchronized (SStorageUtils.this) {
            tempCacheData = (Map<String, Object>) sp.getAll();
            if (mMemoryCacheData != null && !mMemoryCacheData.isEmpty()) {
                tempCacheData.putAll(mMemoryCacheData);
            }
        }

        return new ArrayList<>(tempCacheData.keySet());
    }

    /**
     * 向内存缓存中存入数据
     */
    private boolean putData2MemoryCache(String key, Object data) {
        if (mMemoryCacheData != null) {
            mMemoryCacheData.put(key, data);
            return true;
        }
        return false;
    }

    /**
     * 从内存缓存中取数据 -- 未来得及提交的数据
     */
    private Object getDataFromMemoryCache(String key) {
        if (mMemoryCacheData != null && mMemoryCacheData.containsKey(key)) {
            return mMemoryCacheData.get(key);
        }
        return null;
    }

    /**
     * apply提交数据时，放入单线程池中，执行时清除内存缓存
     */
    private void apply(final SharedPreferences.Editor editor, final String key) {
        getStorageHandler().post(new Runnable() {
            @Override
            public void run() {
                editor.commit();
                synchronized (SStorageUtils.this) {
                    mMemoryCacheData.remove(key);
                }
            }
        });
    }

    private void submitEditor(SharedPreferences.Editor editor, String key) {
        apply(editor, key);
    }

}
