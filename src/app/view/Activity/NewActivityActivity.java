package app.view.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import app.view.login.R;


/**
 * 新建活动页
 * Created by lxs on 2014/8/6.
 */

public class NewActivityActivity extends Activity {

    private ImageView photo;
    private Button enddate, endtime, activitylocation, invitebutton, postbutton;
    private EditText activityname;

    private static final int DATE_PICKER_ID = 1;
    private static final int TIME_PICKER_ID = 2;

    private int DATE_YEAR;
    private int DATE_MONTH;
    private int DATE_DAY;
    private int HOUR;
    private int MINUTE;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newactivity_activity_layout);

        photo = (ImageView) findViewById(R.id.newplay_photo);
        activityname = (EditText) findViewById(R.id.newplay_activityname);
        enddate = (Button) findViewById(R.id.newplay_enddate);
        endtime = (Button) findViewById(R.id.newplay_endtime);
        activitylocation = (Button) findViewById(R.id.newplay_activitylocation);
        invitebutton = (Button) findViewById(R.id.newplay_invite_button);
        postbutton = (Button) findViewById(R.id.newplay_post_button);

        dateSetButtonListener datesetlistener = new dateSetButtonListener();
        timeSetButtonListener timesetlistener = new timeSetButtonListener();
        enddate.setOnClickListener(datesetlistener);
        endtime.setOnClickListener(timesetlistener);
    }

    //选择活动图片监听器
    class PhotoListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }
    }

    private class dateSetButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            showDialog(DATE_PICKER_ID);
        }
    }

    private class timeSetButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            showDialog(TIME_PICKER_ID);
        }
    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            DATE_YEAR = year;
            DATE_MONTH = monthOfYear + 1;
            DATE_DAY = dayOfMonth;
            enddate.setText("活动截止时间：" + DATE_YEAR + "年" + DATE_MONTH + "月" + DATE_DAY + "日");
        }
    };

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            HOUR = hourOfDay;
            MINUTE = minute;
            endtime.setText(HOUR + ":" + MINUTE);
        }
    };

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, onDateSetListener, 2014, 4, 1);
            case TIME_PICKER_ID:
                return new TimePickerDialog(this, onTimeSetListener, 00, 00, true);
        }
        return null;
    }
}
