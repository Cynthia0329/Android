package com.cqnu.chenyudan.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.util.OderDBHelper;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    private OderDBHelper helper;

    public MainService() {
    }

    /*返回IBinder*/
    @Override
    public IBinder onBind(Intent intent) {
        return new MainServiceBinder();
    }

    /*获取Service的方法*/
    public class MainServiceBinder extends Binder {
        public MainService getService(){
            return MainService.this;
        }
    }

    /*解除绑定*/
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        helper = new OderDBHelper(MainService.this);     //调用OderDBHelper的构造方法
        SQLiteDatabase db = helper.getReadableDatabase();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /******************************方法*******************************************/
    //测试方法
    public void to(){
        Toast.makeText(this,"测试成功！", Toast.LENGTH_SHORT).show();
    }


    /**
     * 查询并返回所有联系人的信息
     * @return  返回联系人列表
     */
    public List<ContactInfo> queryAll() {
        SQLiteDatabase db = helper.getReadableDatabase();   //获取用于操作数据库的SQLiteDatabase实例

        Cursor cursor = db.query("contactinfo", new String[]{"name, phone, email, street, city, nickname, company, weixin"}, null, null, null, null, null);
        List<ContactInfo> infos = new ArrayList<>();
        while (cursor.moveToNext()) {
            ContactInfo info = new ContactInfo();
            info.setName(cursor.getString(0));
            info.setPhone(cursor.getString(1));
            info.setEmail(cursor.getString(2));
            info.setStreet(cursor.getString(3));
            info.setCity(cursor.getString(4));
            info.setNickname(cursor.getString(5));
            info.setCompany(cursor.getString(6));
            info.setWeixin(cursor.getString(7));

            infos.add(info);
        }

        cursor.close();
        db.close();

        return infos;
    }


    /**
     *
     * @param name  联系人名字
     * @return      返回记录的行号
     */
    public int delete(String name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowcount = db.delete("contactinfo", "name=?", new String[]{name});
        db.close();
        return rowcount;
    }


    /**
     * 删除全部联系人信息
     */
    public void deleteAll(){
        SQLiteDatabase db = helper.getWritableDatabase();
        int i = db.delete("contactinfo", null, null);
        db.close();

    }


    /**
     * 添加新的联系人
     * @param info      联系人列表
     * @return          返回记录的行号
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



    /**
     * 查询并返回联系人电话
     * @param name          联系人名字
     * @return              联系人电话
     */
    public String queryPhone(String name) {
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
        return info.getPhone();
    }


    /**
     * 查询联系人信息
     * @param name      联系人信息
     * @return          联系人信息列表
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

//
//    /**
//     * 打电话
//     * @param dName     联系人名字
//     * @param context   上下文
//     */
//    public void tel(String dName, final Context context){
//
//        final String number = queryPhone(dName);
//        if ("".equals(number)) {
//            Toast.makeText(context,"该联系人的电话号码为空！", Toast.LENGTH_SHORT).show();
//        } else {
//            //判断是否获取权限
//        }
//
//    }
//
//
//    /**
//     * 发送短信
//     * @param dName     联系人名字
//     * @param context   上下文
//     */
//    public void sms(String dName, final Context context){
//
//        final String number = queryPhone(dName);
//        if ("".equals(number)){
//            Toast.makeText(context,"该联系人的电话号码为空！", Toast.LENGTH_SHORT).show();
//        }else {
//            final EditText editText = new EditText(this);
//            AlertDialog.Builder inputDialog = new AlertDialog.Builder(context);
//            inputDialog.setTitle("请输入你要对联系人 " + dName + "发送的信息").setView(editText);
//            inputDialog.setPositiveButton("发送短信",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            final String message = editText.getText().toString().trim();    //获得输入框的短信内容
//                            if ("".equals(message)) {
//                                Toast.makeText(context,"你输入的内容为空！", Toast.LENGTH_SHORT).show();
//                            } else {
//                                //判断是否获取权限
//
//                            }
//
//                        }
//                    }).show();
//
//    }
//
//
//
//    }
}
