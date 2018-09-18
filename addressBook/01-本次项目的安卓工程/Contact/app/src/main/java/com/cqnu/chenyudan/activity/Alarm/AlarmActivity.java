package com.cqnu.chenyudan.activity.Alarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TimePicker;

import com.cqnu.chenyudan.R;

/**
 * 事务提醒，设置提醒时间
 */
public class AlarmActivity extends AppCompatActivity {

    private TimePicker alarmTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        /*寻找时间控件*/
        alarmTimePicker = (TimePicker) findViewById(R.id.alarm_time_tp);
    }

    /*点击进入编辑事务提醒内容*/
    public void edit(View view) {
        //获取要设置的提醒时间
        String hour = alarmTimePicker.getCurrentHour().toString();
        String minute = alarmTimePicker.getCurrentMinute().toString();

        Intent intent = new Intent(AlarmActivity.this, AlarmEditActivity.class);
        //向下一个页面传入提醒时间
        intent.putExtra("hour",hour);
        intent.putExtra("minute",minute);
        AlarmActivity.this.startActivity(intent);

    }
}
