package com.cqnu.chenyudan.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.cqnu.chenyudan.util.OderDBHelper;

/**
 * 注册界面
 */
public class RegisterService extends Service {

    private OderDBHelper helper;

    public RegisterService() {
    }

    /*返回IBinder*/
    @Override
    public IBinder onBind(Intent intent) {
        return new RegisterServiceBinder();
    }

    /*获取Service的方法*/
    public class RegisterServiceBinder extends Binder {
        public RegisterService getService(){
            return RegisterService.this;
        }
    }

    /*解除绑定*/
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        helper = new OderDBHelper(RegisterService.this);     //调用OderDBHelper的构造方法
        SQLiteDatabase db = helper.getReadableDatabase();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /******************************方法*******************************************/
    /*测试方法*/
    public void to(){
        Toast.makeText(this,"测试成功！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 插入并保存注册信息
     * @param number        注册账号
     * @param password      注册密码
     */
    public void add(String number,String password) {
        SQLiteDatabase db = helper.getWritableDatabase();   //获取用于操作数据库的SQLiteDatabase实例

        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("password",password);
        long rowid = db.insert("user", null, values);
        db.close();
    }
}
