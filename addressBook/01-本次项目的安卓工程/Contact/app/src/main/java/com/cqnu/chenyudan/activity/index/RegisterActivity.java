package com.cqnu.chenyudan.activity.index;

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
import android.widget.EditText;
import android.widget.Toast;

import com.cqnu.chenyudan.R;
import com.cqnu.chenyudan.service.RegisterService;

/**
 * 注册界面
 */
public class RegisterActivity extends AppCompatActivity {

    /*绑定成功后，从Binder对象中得到service*/
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RegisterService.RegisterServiceBinder registerBinder = (RegisterService.RegisterServiceBinder) iBinder;
            service = registerBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private RegisterService service;
    private EditText et_number;
    private EditText et_password;
    private EditText et_passwoed2;
    private String mUserNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        getSupportActionBar().hide();
        //Activity样式文件,一定要写在中间
        setContentView(R.layout.activity_register);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /*发起对Service的绑定*/
        Intent intent = new Intent(this, RegisterService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);

        initView(); //初始化界面
    }

    /*初始化界面 获取屏幕上的控件*/
    private void initView() {
        et_number = (EditText) findViewById(R.id.et_number);
        et_password = (EditText) findViewById(R.id.et_passwd);
        et_passwoed2 = (EditText) findViewById(R.id.et_password2);

    }


    /*点击注册*/
    public void register(View view) {
        //得到控件中的数据
        String userNum =  et_number.getText().toString().trim();
        if (TextUtils.isEmpty(userNum)) {
            Toast.makeText(this,"账号不能为空！", Toast.LENGTH_SHORT).show();
        }
        String userPsw =  et_password.getText().toString().trim();
        if (TextUtils.isEmpty(userPsw)) {
            Toast.makeText(this,"密码不能为空！", Toast.LENGTH_SHORT).show();
        }
        String userPsw2 = et_passwoed2.getText().toString().trim();
        if (TextUtils.isEmpty(userPsw2)) {
            Toast.makeText(this,"确认密码不能为空！", Toast.LENGTH_SHORT).show();
        }
        if(!userPsw.equals(userPsw2)){
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return ;
        }else{
            service.add(userNum,userPsw);
            Toast.makeText(this,"完成注册！", Toast.LENGTH_SHORT).show();
        }

    }

    /*返回登陆界面*/
    public void returnLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        this.startActivity(intent);
    }
}
