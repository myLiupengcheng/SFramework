package com.suansuan.sframework.dao.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class BaseDataBaseOpenHelper extends SQLiteOpenHelper{

    public static final String VERSION_TABLE_NAME = "version";
    public static final String HOTEL_VERSION = "hv";
    public static final String VERSION_NAME = "name";
    public static final String VERSION_CODE = "value";

    public static final int VERSION = 1;


    public BaseDataBaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }
}
