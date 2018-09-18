package com.cqnu.chenyudan.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 该类用于建数据库和建表
 */
public class OderDBHelper extends SQLiteOpenHelper {


    public OderDBHelper(Context context) {
        super(context, "sqlName.db", null, 1);
    }


    /**
     * 当数据库第一次被创建的时候调用的方法，在这个方法里面定义数据库的表结构
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("创建了一张数据表sqlName");
        //创建一张用户账号表
        String user = ("create table user(id integer primary key autoincrement,number integer(20),password integer(20));");
        db.execSQL(user);
        //创建一张联系人信息表
        String sql = ("create table contactinfo (id integer primary key autoincrement, name char(20)," +
                " phone varchar(20), email varchar(20), street varchar(20), city varchar(20)," +
                "nickname varchar(20), company varchar(20), weixin varchar(20));");
        db.execSQL(sql);

    }

    /**
     * 当数据库更新的时候调用的方法
     * @param db
     * @param oldVersion    旧版本
     * @param newVersion    新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("数据库被更新了！");

    }
}
