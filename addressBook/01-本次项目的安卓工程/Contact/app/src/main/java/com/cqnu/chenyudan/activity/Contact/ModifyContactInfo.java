package com.cqnu.chenyudan.activity.Contact;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cqnu.chenyudan.R;
import com.cqnu.chenyudan.activity.MainActivity;
import com.cqnu.chenyudan.model.ContactInfo;
import com.cqnu.chenyudan.service.ModifyService;

/**
 * 修改联系人信息
 */
public class ModifyContactInfo extends AppCompatActivity {


    /*绑定成功后，从Binder对象中得到service*/
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ModifyService.ModifyServiceBinder modifyBinder = (ModifyService.ModifyServiceBinder) iBinder;
            service = modifyBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    //获取控件
    private EditText et_name;
    private EditText et_phone;
    private EditText et_email;
    private EditText et_street;
    private EditText et_city;
    private EditText et_nickname;
    private EditText et_company;
    private EditText et_weixin;
    private Button bt_save;

    private ModifyService service;  //绑定service
    private ContactInfo info;   //查询得到联系人列表
    private String name;    //得到传入的联系人名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        initData();
        initView();
    }

    //初始化数据
    private void initData() {
        /*发起对Service的绑定*/
        Intent mintent = new Intent(this, ModifyService.class);
        bindService(mintent,conn, Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();
        //得到传入的联系人名字
        name = intent.getStringExtra("name");

        new Thread(){
            @Override
            public void run() {
                // 等待服务连接成功，连接成功后就不为null,会跳出循环执行下面的代码
                while (service == null){
                    SystemClock.sleep(100);
                }
                //这里是在子线程当中，所以可以做耗时操作
                if (service != null){
                    /*查询得到该联系人的所有信息*/
                    info = service.query(name);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //这里是在ui线程不能做耗时操作，只能做和ui相关的事，比如将获取到的数据显示到界面
                            if (info != null){
                                info.setName(name);
                                setContactInfo(info);
                            }
                        }
                    });
                }
            }
        }.start();


    }


    /*初始化界面 获取屏幕上的控件*/
    private void initView() {
        //取消标题
        getSupportActionBar().hide();
        setContentView(R.layout.activity_contact_info); //设置显示界面
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        et_name = (EditText) findViewById(R.id.et_name);    //获取屏幕上的控件
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_street = (EditText) findViewById(R.id.et_street);
        et_city = (EditText) findViewById(R.id.et_city);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_company = (EditText) findViewById(R.id.et_company);
        et_weixin = (EditText) findViewById(R.id.et_weixin);
        bt_save = (Button) findViewById(R.id.bt_save);
    }

    /*设置界面所有EditText文本内容    返回联系人对象*/
    private void setContactInfo(ContactInfo info) {
        et_name.setText(info.getName());
        et_phone.setText(info.getPhone());
        et_email.setText(info.getEmail());
        et_street.setText(info.getStreet());
        et_city.setText(info.getCity());
        et_nickname.setText(info.getNickname());
        et_company.setText(info.getCompany());
        et_weixin.setText(info.getWeixin());
    }

    /* 获取界面所有EditText文本内容*/
    private ContactInfo getContactInfo() {
        String name =  et_name.getText().toString().trim();
        String phone =  et_phone.getText().toString().trim();
        String email =  et_email.getText().toString().trim();
        String street =  et_street.getText().toString().trim();
        String city =  et_city.getText().toString().trim();
        String nickname =  et_nickname.getText().toString().trim();
        String company =  et_company.getText().toString().trim();
        String weixin =  et_weixin.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this,"姓名不能为空", Toast.LENGTH_SHORT).show();
            return null;
        }

        return new ContactInfo(name, phone, email, street, city, nickname, company, weixin);
    }

    /*保存更新的信息*/
    public void save(View view) {
        ContactInfo minfo = getContactInfo();   //得到界面上的数据
        service.update(minfo);
        Toast.makeText(this,"更新联系人信息成功！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
