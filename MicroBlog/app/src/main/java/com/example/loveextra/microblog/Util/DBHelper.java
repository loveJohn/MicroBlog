package com.example.loveextra.microblog.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LoveExtra on 2016/6/19.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int VERSION=1;
    public static final String DATA_MB="MB_DATA";               //BT数据库

    public static final String TABLE_NOTE="MB_NOTE";  //表名note

    public static final String  NOTE_ID="_ID_NOTE";
    public static final String  NOTE_CONTENT="CONTENT_NOTE";
    public static final String  NOTE_DATE_STAMP="DATE_STAMP_NOTE";



    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表note的sql语句
        String sqlNote="create table "+TABLE_NOTE+"("+NOTE_ID+" Integer primary key,"+NOTE_CONTENT+" varchar,"+NOTE_DATE_STAMP+" varchar);";

        db.execSQL(sqlNote);    //执行sql命令，建表

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
