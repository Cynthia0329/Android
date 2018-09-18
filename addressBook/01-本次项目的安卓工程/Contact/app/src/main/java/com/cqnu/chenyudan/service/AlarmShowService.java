package com.cqnu.chenyudan.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.util.OderDBHelper;

public class AlarmShowService extends Service {

    private OderDBHelper helper;

    public AlarmShowService() {
    }

    /*返回IBinder*/
    @Override
    public IBinder onBind(Intent intent) {
        return new AlarmShowServiceBinder();
    }

    /*获取Service的方法*/
    public class AlarmShowServiceBinder extends Binder {
        public AlarmShowService getService(){
            return AlarmShowService.this;
        }
    }

    /*解除绑定*/
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        helper = new OderDBHelper(AlarmShowService.this);   //调用OderDBHelper的构造方法
        SQLiteDatabase db = helper.getReadableDatabase();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /******************************方法*******************************************/
     /*查询联系人信息*/
    public ContactInfo query(String name) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query( "contactinfo", new String[]{"phone", "email", "street", "city", "nickname", "company", "weixin"}, "name=?", new String[]{name}, null, null, null);
        if (cursor.getCount() == 0) return null;
        ContactInfo info = new ContactInfo();

        if (cursor.moveToNext()) {
            info.setPhone(cursor.getString(0));
            info.setEmail(cursor.getString(1));
            info.setStreet(cursor.getString(2));
            info.setCity(cursor.getString(3));
            info.setNickname(cursor.getString(4));
            info.setCompany(cursor.getString(5));
            info.setWeixin(cursor.getString(6));
        }

        cursor.close();
        db.close();
        return info;
    }
}
