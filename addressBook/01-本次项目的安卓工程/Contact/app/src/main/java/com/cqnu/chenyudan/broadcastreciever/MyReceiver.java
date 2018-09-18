package com.cqnu.chenyudan.broadcastreciever;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cqnu.chenyudan.R;
import com.cqnu.chenyudan.activity.Alarm.AlarmShowActivity;

/**
 * 广播接收器，接收发送过来的广播
 */
public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        /* an Intent broadcast.*/
        String actionStr=intent.getAction();    //接收动作
        if(actionStr.equals("myAlarm01")){
            System.out.println("in myreceiver");
            /*将提醒信息显示在通知栏中*/
            NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //创建Notification（通知）
            Notification.Builder noticeBuilder=new Notification.Builder(context);
            noticeBuilder.setSmallIcon(R.mipmap.tixi);
            noticeBuilder.setContentTitle("你收到一个事务提醒");
            /*得到action动作传入的数据*/
            String title = intent.getStringExtra("title");
            String name = intent.getStringExtra("name");
            String content=intent.getStringExtra("content");
            //设置通知显示的内容
            noticeBuilder.setContentText(title);
            /*定义了点击通知后的动作（打开一个Activity）*/
            Intent showMsgIn=new Intent(context,AlarmShowActivity.class);
            //再次将数据发送
            showMsgIn.putExtra("title",title);
            showMsgIn.putExtra("name",name);
            showMsgIn.putExtra("content",content);
            PendingIntent pi= PendingIntent.getActivity(context,0,showMsgIn,PendingIntent.FLAG_UPDATE_CURRENT);
            //将点击通知后的动作配置到通知对象上
            noticeBuilder.setContentIntent(pi);
            //创建通知对象（Notification）
            Notification notice=noticeBuilder.build();
            //将通知放入通知栏
            nm.notify(0,notice);
        }else{
            Toast t= Toast.makeText(context,intent.getStringExtra("msg"), Toast.LENGTH_LONG);
            t.show();
        }

    }
}
