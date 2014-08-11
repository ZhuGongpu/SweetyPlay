package app.view.signup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import app.view.login.R;
import app.view.main.MainActivity;
import avos.AVOSWrapper;
import avos.callbackwrappers.LogInCallbackWrapper;
import avos.callbackwrappers.SignUpCallbackWrapper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;

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
    private EditText country_EditText = null;
    private Button play_Button = null;
    private ImageView male_ImageView, female_ImageView;

    private String nickName, countryOrArea, email, phone_number, password, birthday;
    private int yearOfBirth, monthOfBirth, dayOfBirth;
    private OnDateSetListener mDateListener = new OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
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
                else if (!datePicker.isSelected())
                    Toast.makeText(getApplicationContext(), "you should pick your birthday first", Toast.LENGTH_SHORT).show();
                else if (gender == Gender.unknown)
                    Toast.makeText(getApplicationContext(), "you should choose your gender first", Toast.LENGTH_SHORT).show();
                else if (!isEmail(email)) {
                    email_EditText.setError("Not Valid");
                } else//注册信息完整
                {
                    nickName = nickname_EditText.getText().toString();

                    birthday = birthday_EditText.getText().toString();
                    countryOrArea = country_EditText.getText().toString();
                    email = email_EditText.getText().toString();

                    //初始化avos
                    AVOSWrapper.init(SignUpActivity.this);

                    //set user info
                    AVUser user = new AVUser();
                    user.setEmail(email);
                    user.setPassword(password);

                    user.put("birthday", birthday);
                    user.put("gender", gender);
                    user.put("phone_number", phone_number);
                    user.put("country", countryOrArea);

                    //sign up
                    AVOSWrapper.signUpInBackground(user, new SignUpCallbackWrapper() {
                        @Override
                        public void onSucceed() {

                            //succeed
                            AVOSWrapper.logInInBackground(phone_number, password, new LogInCallbackWrapper() {

                                @Override
                                public void onSucceed(AVUser user) {

                                    // jump to friendlist activity
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onFailed(AVException e) {

                                    // notify with error
                                    promoteErrorMessage(getText(R.string.failed).toString());
                                }
                            });
                        }

                        @Override
                        public void onFailed(AVException e) {

                            //提示注册异常
                            promoteErrorMessage(getText(R.string.failed).toString());
                        }
                    });

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
