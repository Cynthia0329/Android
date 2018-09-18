package com.cqnu.chenyudan.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.util.OderDBHelper;

import java.util.Calendar;

public class AlarmEditService extends Service {

    private OderDBHelper helper;

    public AlarmEditService() {
    }

    /*返回IBinder*/
    @Override
    public IBinder onBind(Intent intent) {
        return new AlarmEditServiceBinder();
    }

    /*获取Service的方法*/
    public class AlarmEditServiceBinder extends Binder {
        public AlarmEditService getService(){
            return AlarmEditService.this;
        }
    }

    /*解除绑定*/
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        helper = new OderDBHelper(this);
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
     * 设置事务提醒
     * @param hour      小时
     * @param minute    分钟
     * @param title     标题
     * @param name      联系人名字
     * @param content   内容
     * @return          设置成功则返回true
     */
    public boolean setAlarm(String hour, String minute, String title, String name, String content){

        //获得提醒管理对象
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //设置提醒的动作（PendingIntent）
        Intent intent = new Intent();
        intent.setAction("myAlarm01");
        System.out.println("title:" + title);
        System.out.println("name:" + name);
        System.out.println("content:" + content);
        System.out.println("hour:" + hour);
        System.out.println("minute:" + minute);

        intent.putExtra("title",title);
        intent.putExtra("name",name);
        intent.putExtra("content",content);
        //设置提醒的动作（发送广播）
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  //PendingIntent.FLAG_UPDATE_CURRENT:
                                                                                                            //描述的Intent有 更新的时候需要用到这个flag去更新你的描述，否则组件在下次事件发生或时间到达的时候extras永远是第一次Intent的extras
        //设置提醒时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        calendar.set(calendar.SECOND,0);
        long timeInMillis = calendar.getTimeInMillis();
        //设置提醒
        am.set(AlarmManager.RTC_WAKEUP,timeInMillis,pi);

        return true;
    }


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
