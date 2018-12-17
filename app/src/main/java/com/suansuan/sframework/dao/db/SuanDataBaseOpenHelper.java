package com.suansuan.sframework.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

@SuppressWarnings("all")
public class SuanDataBaseOpenHelper extends BaseDataBaseOpenHelper{

    private static SuanDataBaseOpenHelper sInstance = null;
    private static String DB_NAME = null;

    public static SuanDataBaseOpenHelper newInstance(Context context, String dataBaseName) {
        if(sInstance == null){
            synchronized (SuanDataBaseOpenHelper.class){
                if(SuanDataBaseOpenHelper.isDataBasePrepare(context, dataBaseName)){

                }
            }
        }
        return sInstance;
    }


    private SuanDataBaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static boolean isDataBasePrepare(Context context, String dataBaseName){
        File databasePath = context.getDatabasePath(dataBaseName + ".db");
        return databasePath.exists();
    }
}
