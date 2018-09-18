package com.cqnu.chenyudan.activity.Alarm;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cqnu.chenyudan.R;
import com.cqnu.chenyudan.activity.MainActivity;
import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.service.AlarmShowService;
import com.cqnu.chenyudan.util.BaseActivity;

public class AlarmShowActivity extends BaseActivity {



    /*绑定成功后，从Binder对象中得到service*/
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AlarmShowService.AlarmShowServiceBinder showBinder = (AlarmShowService.AlarmShowServiceBinder) iBinder;
            service = showBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private TextView tv_number;
    private TextView tv_title;
    private TextView tv_name;
    private TextView tv_content;

    private AlarmShowService service;
    private String name;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();

    }

    /*初始化数据*/
    private void initData() {
        if (AlarmEditActivity.service != null) {
            /*发起对service的绑定*/
            Intent intent = new Intent(this, AlarmShowService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
        }
        new Thread(){
            @Override
            public void run() {
                // 等待服务连接成功，连接成功后就不为null,会跳出循环执行下面的代码
                while (service == null){
                    SystemClock.sleep(100);
                }
                //这里是在子线程当中，所以可以做耗时操作
                if (service != null){
                    final ContactInfo info = service.query(name);//查询得到该联系人的所有信息
                    phone = info.getPhone();//得到该联系人的号码
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //这里是在ui线程不能做耗时操作，只能做和ui相关的事，比如将获取到的数据显示到界面
                            if (info != null){
                                tv_number.setText(phone);   //设置显示查找到的号码
                            }
                        }
                    });
                }
            }
        }.start();


    }

    /*初始化界面*/
    private void initView() {
        setContentView(R.layout.activity_alarm_show);
        //找到所有控件
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_number = (TextView) findViewById(R.id.tv_number);

        tv_number.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);   //给电话号码增加下划线
        //设置界面内容
        setShowInfo();
    }


    /*设置界面TextView的内容*/
    public void setShowInfo(){
        //接受传来的数据
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        name = intent.getStringExtra("name");
        String content = intent.getStringExtra("content");

        //设置界面
        tv_title.setText(title);
        tv_name.setText(name);
        tv_content.setText(content);
    }


    /*发送短信*/
    public void sms(View view) {
        final EditText editText = new EditText(this);
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setTitle("请输入你要对联系人 " + name + "发送的信息").setView(editText);
        inputDialog.setPositiveButton("发送短信",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String message = editText.getText().toString().trim();    //获得输入框的短信内容
                        if ("".equals(message)) {
                            Toast.makeText(AlarmShowActivity.this,"你输入的内容为空！", Toast.LENGTH_SHORT).show();
                        } else {

                            performCodeWithPermission("发送短信权限", new PermissionCallback() {
                                @Override
                                public void hasPermission() {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(phone,null,message,null,null);
                                }

                                @Override
                                public void noPermission() {

                                }
                            },Manifest.permission.SEND_SMS);
                        }

                    }
                }).show();

    }

    /*拨打电话*/
    public void tel(View view) {
        performCodeWithPermission("拨打电话权限", new PermissionCallback() {
            @Override
            public void hasPermission() {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel://" + phone));
                startActivity(intent);
            }

            @Override
            public void noPermission() {

            }
        }, Manifest.permission.CALL_PHONE);
    }

    /*返回主界面*/
    public void click(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
