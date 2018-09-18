package com.cqnu.chenyudan.activity.Contact;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.cqnu.chenyudan.service.AddService;

/**
 * 添加联系人
 */
public class AddContactInfo extends AppCompatActivity {

    /*绑定成功后从Binder对象中得到Service*/
    private ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AddService.AddContactInfoServiceBinder addBinder = (AddService.AddContactInfoServiceBinder) iBinder;
            dao = addBinder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private AddService dao;
    private EditText et_name;
    private EditText et_phone;
    private EditText et_email;
    private EditText et_street;
    private EditText et_city;
    private EditText et_nickname;
    private EditText et_company;
    private EditText et_weixin;
    private Button bt_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*发起对Service的绑定*/
        Intent intent = new Intent(this, AddService.class);
        bindService(intent,con, Context.BIND_AUTO_CREATE);

        initView();
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

    /*获取界面所有文本的内容*/
    private ContactInfo getContactInfo(){
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


    /*点击保存监听*/
    public void save(View view) {

        ContactInfo info = getContactInfo();
        dao.add(info);
        Toast.makeText(this,"添加联系人成功！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

}