package com.cqnu.chenyudan.activity.Alarm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cqnu.chenyudan.R;
import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.service.AlarmEditService;

public class AlarmEditActivity extends AppCompatActivity {

    /*绑定成功后，从Binder对象中得到service*/
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AlarmEditService.AlarmEditServiceBinder editBinder = (AlarmEditService.AlarmEditServiceBinder) iBinder;
            service = editBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private String hour;
    private String minute;
    private EditText et_title;
    private EditText et_name;
    private EditText et_content;
    public static AlarmEditService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*发起对Service的绑定*/
        Intent intent = new Intent(this, AlarmEditService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);

        initView();
        initData();
    }

    /*初始化界面*/
    private void initView() {
        setContentView(R.layout.activity_alarm_edit);
        //找到控件
        et_title = (EditText) findViewById(R.id.et_title);
        et_name = (EditText) findViewById(R.id.et_name);
        et_content = (EditText) findViewById(R.id.et_content);

    }

    /*初始化数据*/
    private void initData() {
        //得到上个页面传入的时间
        Intent intent = getIntent();
        hour = intent.getStringExtra("hour");
        minute = intent.getStringExtra("minute");
    }


    public void submit(View view) {
        /*得到界面上的信息*/
        String title =  et_title.getText().toString().trim();
        String name =  et_name.getText().toString().trim();
        String content =  et_content.getText().toString().trim();

        ContactInfo queryName = service.query(name);
        if (queryName == null) {
            Toast.makeText(this, "你输入的联系人不存在，请重新输入！", Toast.LENGTH_SHORT).show();
        }else{
            boolean setAlarmRe = service.setAlarm(hour, minute, title, name, content);
            if(setAlarmRe){
                Toast t=Toast.makeText(AlarmEditActivity.this,"设置事务成功",Toast.LENGTH_LONG);
                t.show();
            }
        }


    }
}
