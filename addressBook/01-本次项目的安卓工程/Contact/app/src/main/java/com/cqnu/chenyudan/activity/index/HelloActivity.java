package com.cqnu.chenyudan.activity.index;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.cqnu.chenyudan.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 欢迎界面
 */
public class HelloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        getSupportActionBar().hide();
        //Activity样式文件,一定要写在中间
        setContentView(R.layout.activity_hello);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Timer timer=new Timer();    //设置一个定时器
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                Intent intent1=new Intent(HelloActivity.this,LoginActivity.class);
                startActivity(intent1);
                HelloActivity.this.finish();
            }
        };
        timer.schedule(timerTask,1000*3);
    }


}
