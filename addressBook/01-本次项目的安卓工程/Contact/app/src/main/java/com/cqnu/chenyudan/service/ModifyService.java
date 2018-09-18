package com.cqnu.chenyudan.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.util.OderDBHelper;

/**
 * 修改联系人信息
 */
public class ModifyService extends Service {

    private OderDBHelper helper;

    public ModifyService() {
    }

    /*返回IBinder*/
    @Override
    public IBinder onBind(Intent intent) {
        return new ModifyServiceBinder();
    }

    /*获取Service的方法*/
    public class ModifyServiceBinder extends Binder {
        public ModifyService getService(){
            return ModifyService.this;
        }
    }
    /*解除绑定*/
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        helper = new OderDBHelper(ModifyService.this);     //调用OderDBHelper的构造方法
        SQLiteDatabase db = helper.getReadableDatabase();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /******************************方法*******************************************/

    /**
     * 查询联系人
     * @param name  联系人名字
     * @return      联系人信息列表
     */
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


    /*修改联系人信息*/

    /**
     * 修改联系人
     * @param info      联系人信息列表
     * @return          返回记录的行号
     */
    public int update(ContactInfo info) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("phone", info.getPhone());
        values.put("email", info.getEmail());
        values.put("street", info.getStreet());
        values.put("city", info.getCity());
        values.put("nickname", info.getNickname());
        values.put("company", info.getCompany());
        values.put("weixin", info.getWeixin());

        int rowcount = db.update("contactinfo", values, "name=?", new String[]{info.getName()});

        db.close();
        return rowcount;
    }
}
