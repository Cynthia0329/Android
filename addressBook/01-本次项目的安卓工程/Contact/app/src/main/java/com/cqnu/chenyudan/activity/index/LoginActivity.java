package com.cqnu.chenyudan.activity.index;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cqnu.chenyudan.R;
import com.cqnu.chenyudan.activity.MainActivity;
import com.cqnu.chenyudan.service.LoginService;

/**
 * 登陆界面
 */
public class LoginActivity extends AppCompatActivity {

    private EditText et_number;
    private EditText et_passwd;
    private CheckBox cb_remember;

    private SharedPreferences sp;   //定义一个共享参数（存放数据方便的api）
    private LoginService ser;

    /*绑定成功后，从Binder对象中得到service*/
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LoginService.loginserviceBinder loginBinder = (LoginService.loginserviceBinder) iBinder;
            ser = loginBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //取消标题
        getSupportActionBar().hide();
        //Activity样式文件,一定要写在中间
        setContentView(R.layout.activity_login);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

       /*发起对Service的绑定*/
        Intent intent = new Intent(this, LoginService.class);
        bindService(intent,sc, Context.BIND_AUTO_CREATE);

        /*通过上下文得到一个共享参数的实例对象*/
        sp = this.getSharedPreferences("userinfo", this.MODE_PRIVATE);

        //获取控件
        et_number = (EditText) findViewById(R.id.et_number);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        cb_remember = (CheckBox) findViewById(R.id.cb_remember);

        //显示sp中的信息
        restoreInfo();
    }

    /*显示sp中保存的账号信息*/
    private void restoreInfo() {
        String user = sp.getString("user","");
        String password = sp.getString("password","");
        et_number.setText(user);
        et_passwd.setText(password);
        }

    /*点击登陆*/
    public void login(View view) {
        //获取参数
        String user = et_number.getText().toString().trim();
        String password = et_passwd.getText().toString().trim();

            /*判断是否需要记住用户名和密码*/
            boolean checkBoxLogin = cb_remember.isChecked();
            ser.ischecked(user,password,checkBoxLogin);

            /*判断输入的用户名是否正确*/
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(password)) {
            Toast.makeText(this,"用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
        }else{
            if (ser.islogin(user,password)){//ser.islogin(user,password)
                //账号正确 进入主界面
                Intent in=new Intent(this,MainActivity.class);
                this.startActivity(in);
                Toast.makeText(this,"登陆成功！", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"你输入的账号和密码不正确！", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*点击注册*/
    public void sign(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        this.startActivity(intent);
    }
}

