package com.cqnu.chenyudan.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.util.OderDBHelper;

/**
 * 添加联系人
 */
public class AddService extends Service {

    private OderDBHelper helper;

    public AddService() {
    }

    /*返回IBinder*/
    @Override
    public IBinder onBind(Intent intent) {
        return new AddContactInfoServiceBinder();
    }

    /*获取Service的方法*/
    public class AddContactInfoServiceBinder extends Binder {
        public AddService getService(){
            return AddService.this;
        }
    }

    /*解除绑定*/
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        helper = new OderDBHelper(AddService.this);     //调用OderDBHelper的构造方法
        SQLiteDatabase db = helper.getWritableDatabase();


    }


    /******************************方法*******************************************/

    /**
     * 保存插入的联系人数据
     * @param info      联系人列表
     * @return          返回新添记录的行号
     */
    public long add(ContactInfo info) {
        SQLiteDatabase db = helper.getWritableDatabase();   //获取用于操作数据库的SQLiteDatabase实例

        ContentValues values = new ContentValues();
        values.put("name", info.getName());
        values.put("phone", info.getPhone());
        values.put("email", info.getEmail());
        values.put("street", info.getStreet());
        values.put("city", info.getCity());
        values.put("nickname", info.getNickname());
        values.put("company", info.getCompany());
        values.put("weixin", info.getWeixin());

        long rowid = db.insert("contactinfo", null, values);
        db.close();
        return rowid;
    }

    //测试方法
    public void test(){
        Toast.makeText(this,"测试成功！", Toast.LENGTH_SHORT).show();
    }



}
