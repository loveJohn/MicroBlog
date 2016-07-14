package com.example.loveextra.microblog.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;


import com.example.loveextra.microblog.models.Notes;

import java.util.ArrayList;

/**
 * 工具类DataUtils
 * 用于构建数据库保存基本数据
 *
 * Created by LoveExtra on 2016/6/19.
 */

public class DBUtils {

    public static final int VERSION=1;
    public static final String DATA_MB="MB_DATA";               //BT数据库

    public static final String TABLE_NOTE="MB_NOTE";  //表名note

    public static final String  NOTE_ID="_ID_NOTE";
    public static final String  NOTE_CONTENT="CONTENT_NOTE";
    public static final String  NOTE_DATE_STAMP="DATE_STAMP_NOTE";



    //添加笔记
    public static boolean insertNotes(Context context, SQLiteDatabase db, Notes notes){
        ContentValues values=new ContentValues();
        values.put(NOTE_CONTENT,notes.getNoContent());
        values.put(NOTE_DATE_STAMP,notes.getNoDateStamp());
        long ret=db.insert(TABLE_NOTE,null,values);
        if(ret!=-1){
            return true;
        }else {
            return false;
        }
    }

    //删除笔记
    public static boolean deleteNotes(Context context,SQLiteDatabase db,Notes notes){
        if(db.delete(TABLE_NOTE,NOTE_ID+"="+notes.getNoId(),null)!=0){
            return true;
        }else {
            return false;
        }
    }

    public static boolean updateNotes(Context context,SQLiteDatabase db,Notes notes){
        ContentValues values=new ContentValues();
        values.put(NOTE_CONTENT,notes.getNoContent());
        values.put(NOTE_DATE_STAMP,notes.getNoDateStamp());
        int ret=db.update(TABLE_NOTE,values,NOTE_ID+"="+notes.getNoId(),null);
        if(ret!=0){
            return true;
        }else {
            return false;
        }
    }

    public static ArrayList<Notes> queryNotes(Context context,SQLiteDatabase db){
        ArrayList<Notes> list=new ArrayList<>();
        Cursor cursor=db.query(TABLE_NOTE,null,null,null,null,null,null);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            Notes notes=new Notes();
            notes.setNoId(cursor.getInt(cursor.getColumnIndex(NOTE_ID)));
            notes.setNoContent(cursor.getString(cursor.getColumnIndex(NOTE_CONTENT)));
            notes.setNoDateStamp(cursor.getString(cursor.getColumnIndex(NOTE_DATE_STAMP)));
            list.add(notes);
        }
        return list;
    }


}
