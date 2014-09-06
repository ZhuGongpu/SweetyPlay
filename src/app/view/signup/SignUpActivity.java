package app.view.signup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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

    /**
     * UI Elements
     */
    private EditText nickname_EditText = null;
    private EditText email_EditText = null;
    private DatePicker datePicker = null;
    private EditText birthday_EditText = null;
    private Button play_Button = null;
    private ImageView male_ImageView, female_ImageView;

    private String nickName, countryOrArea, email, phone_number, password, birthday, installation_id;
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

    private Gender gender;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity_layout);

        initViews();

        //获取注册信息
        play_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nickName = nickname_EditText.getText().toString();
                email = email_EditText.getText().toString();

                if (!(nickName.length() >= 1 && nickName.length() <= 20))
                    nickname_EditText.setError("Illegal");
                else if (datePicker != null && !datePicker.isSelected())
                    Toast.makeText(getApplicationContext(), "you should pick your birthday first", Toast.LENGTH_SHORT).show();
                else if (gender == Gender.unknown)
                    Toast.makeText(getApplicationContext(), "you should choose your gender first", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(email)) {//TODO check
//                if (!isEmail(email)) {
                    email_EditText.setError("Invalid Email");
                } else//注册信息完整
                {
                    nickName = nickname_EditText.getText().toString();

                    birthday = birthday_EditText.getText().toString();

                    email = email_EditText.getText().toString();

                    //set user info
                    final AVUser user = new AVUser();
                    // user.setEmail(email);//todo
                    user.setUsername(phone_number);
                    user.setPassword(password);

                    try {
                        user.put("birthday", new SimpleDateFormat("yyyy-MM-dd ").parse(birthday));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    user.put("gender", gender);
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

        gender = Gender.unknown;

        male_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male_ImageView.setImageDrawable(getResources().getDrawable(R.drawable.sex_male_ico1));
                female_ImageView.setImageDrawable(getResources().getDrawable(R.drawable.sex_female_ico0));
                gender = Gender.male;
            }
        });

        female_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female_ImageView.setImageDrawable(getResources().getDrawable(R.drawable.sex_female_ico1));
                male_ImageView.setImageDrawable((getResources().getDrawable(R.drawable.sex_male_ico0)));
                gender = Gender.female;
            }
        });

        birthday_EditText.setInputType(InputType.TYPE_NULL);
        setCurrentDate();
        birthday_EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignUpActivity.this,
                        mDateListener, yearOfBirth,
                        monthOfBirth, dayOfBirth).show();

            }
        });

        Intent intent = this.getIntent();
        phone_number = intent.getStringExtra("phoneNumber");
        password = intent.getStringExtra("password");
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
        return m.matches();
    }

    /**
     * 提示错误信息
     *
     * @param message
     */
    private void promoteErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private enum Gender {female, male, unknown}
}
