package app.view.signup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import app.view.login.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import helper.SweetyPlayHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sign up界面
 * 进入此界面之前，已点击loginActivity 中的sign up按钮，并已填写合法的phone number & password
 * 使用时，需将填写好的信息以 phoneNumber 和 password 的形式传入，用于进一步处理（完善信息）
 * Created by zhugongpu on 14-7-7.
 */
public class SignUpActivity extends Activity {

    private static String TAG = "SignUpActivity";
    /**
     * UI Elements
     */
    private EditText nickname_EditText = null;
    private EditText email_EditText = null;
    private EditText birthday_EditText = null;
    private Button play_Button = null;
    private ImageView male_ImageView, female_ImageView;

    private String nickName, email, phone_number, password, birthday, installation_id;
    private int yearOfBirth, monthOfBirth, dayOfBirth;
    private OnDateSetListener mDateListener = new OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            yearOfBirth = year;
            monthOfBirth = monthOfYear;
            dayOfBirth = dayOfMonth;
            //设置文本显示
            updateDisplay();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity_layout);

        initViews();

        //获取注册信息
        play_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(nickname_EditText.getText()) ||
                        !(nickname_EditText.getText().length() >= 1 && nickname_EditText.getText().length() <= 20))
                    nickname_EditText.setError("Illegal");
                else if (TextUtils.isEmpty(birthday_EditText.getText()))
                    Toast.makeText(getApplicationContext(), "you should pick your birthday first", Toast.LENGTH_SHORT).show();
                else if (!(male_ImageView.isSelected() ^ female_ImageView.isSelected()))
                    Toast.makeText(getApplicationContext(), "you should choose your gender first", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(email_EditText.getText()) || !isEmail(email_EditText.getText().toString())) {
                    email_EditText.setError("Invalid Email");
                } else//注册信息完整
                {
                    nickName = nickname_EditText.getText().toString();
                    birthday = birthday_EditText.getText().toString();
                    email = email_EditText.getText().toString();

                    //set user info
                    final AVUser user = new AVUser();
                    user.setEmail(email);
                    user.setUsername(phone_number);
                    user.setPassword(password);

                    try {
                        user.put("birthday", new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (male_ImageView.isSelected())
                        user.put("gender", "male");
                    else if (female_ImageView.isSelected())
                        user.put("gender", "female");

                    user.put("nickName", nickName);

                    //save installation_id
                    installation_id = AVInstallation.getCurrentInstallation().getInstallationId();
                    user.put("installation_id", installation_id);

                    AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            AVInstallation.getCurrentInstallation().saveInBackground();
                        }
                    });

                    //sign up
                    SweetyPlayHelper.register(SignUpActivity.this, user, phone_number, password);
                }
            }
        });
    }

    private void initViews() {//初始化控件

        nickname_EditText = (EditText) findViewById(R.id.nickNameET);
        email_EditText = (EditText) findViewById(R.id.emailET);
        birthday_EditText = (EditText) findViewById(R.id.birthdayET);

        play_Button = (Button) findViewById(R.id.play_button);

        male_ImageView = (ImageView) findViewById(R.id.gender_male);
        female_ImageView = (ImageView) findViewById(R.id.gender_female);

        male_ImageView.setSelected(true);

        birthday_EditText.setInputType(InputType.TYPE_NULL);


        Intent intent = this.getIntent();
        phone_number = intent.getStringExtra("phoneNumber");
        password = intent.getStringExtra("password");
    }

    public void showDatePickerDialog(View v) {
        setCurrentDate();
        new DatePickerDialog(SignUpActivity.this,
                mDateListener, yearOfBirth,
                monthOfBirth, dayOfBirth).show();
    }

    //用来设置显示日期格式
    private void updateDisplay() {
        this.birthday_EditText.setText(new StringBuilder().append(yearOfBirth).append("-")
                .append((monthOfBirth + 1) < 10 ? "0" + (monthOfBirth + 1) :
                        (monthOfBirth + 1)).append("-")
                .append((dayOfBirth < 10) ? "0" + dayOfBirth : dayOfBirth));
    }

    //获得当前时间
    private void setCurrentDate() {
        final Calendar c = Calendar.getInstance();
        yearOfBirth = c.get(Calendar.YEAR);
        monthOfBirth = c.get(Calendar.MONTH);
        dayOfBirth = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    private boolean isEmail(String strEmail) {//邮箱格式检测
        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        boolean result = m.matches();

        Log.e(TAG, "isEmail : " + result);
        return result;
    }

    /**
     * 提示错误信息
     *
     * @param message
     */
    private void promoteErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
