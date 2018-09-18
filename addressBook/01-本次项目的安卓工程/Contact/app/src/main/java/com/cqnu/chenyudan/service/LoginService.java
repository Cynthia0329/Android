package com.cqnu.chenyudan.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import com.cqnu.chenyudan.util.OderDBHelper;

/**
 * 登陆界面
 */
public class LoginService extends Service {

    SharedPreferences sp = null;    //得到SharedPreferences对象
    private OderDBHelper helper;

    public LoginService() {
    }


    /*返回IBinder*/
    @Override
    public IBinder onBind(Intent intent) {
        return new loginserviceBinder();
    }

    /*获取Service的方法*/
    public class loginserviceBinder extends Binder {
        public LoginService getService(){
            return LoginService.this;
        }
    }

    /*解除绑定*/
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }



     @Override
     public void onCreate() {
         sp = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
         helper = new OderDBHelper(LoginService.this);  //调用OderDBHelper的构造方法
     }






   /******************************方法*******************************************/



    /**
     * 方法一、判断输入框的用户名和密码是否正确
     * @param user          传入的账号
     * @param password      传入的密码
     * @return              返回账户信息是否正确的结果
     */
   public boolean islogin(String user, String password) {
       Boolean isLogin = false;
       SQLiteDatabase db = helper.getWritableDatabase();
       Cursor query = db.query("user", new String[]{"number", "password"}, null, null, null, null, null);

       while (query.moveToNext())
            {
               String userstr = null;
               String passwdstr = null;
               userstr = query.getString(0);
               passwdstr = query.getString(1);
               if (userstr.equals(user) && passwdstr.equals(password)){
                   isLogin = true;  //如果正确则返回true
               }
           }
           return isLogin;
   }


    /**
     * 方法二、选中保存账户和密码按钮10天
     * @param user                      传入的账号
     * @param password                  传入的密码
     * @param checkBoxLogin             选项框是否被选中
     */
   public void ischecked(String user, String password,boolean checkBoxLogin){

       if (checkBoxLogin) {
           //3、将数据保存到sp文件中
           SharedPreferences.Editor editor = sp.edit(); //获取一个SharedPreferences.Editor对象
           editor.putString("user",user); //添加数据
           editor.putString("password",password);
           long loginTime = sp.getLong("loginTime", 0);
           //判断一下是否为0，仅仅是初次登陆及超过10天登录时间后清除登录状态后才需要写入当前时间，
           // 如果不判断是否为0，就会每次写入当前时间，这样时间限制就没有效果了
           if (loginTime == 0) {
               editor.putLong("loginTime", System.currentTimeMillis());
           }
           editor.putBoolean("remember", true);
           editor.commit();   //将添加的数据提交


       } else {
           return;
       }

   }























}
