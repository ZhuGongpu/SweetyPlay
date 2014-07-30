package app.view.signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import app.view.login.R;
import app.view.main.MainActivity;
import avos.AVOSWrapper;
import avos.callbackwrappers.LogInCallbackWrapper;
import avos.callbackwrappers.SignUpCallbackWrapper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;

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
    private EditText firstName_editText = null;
    private EditText lastName_editText = null;
    private EditText email_editText = null;
    private RadioButton male_radioButton = null;
    private RadioButton female_radioButton = null;
    private DatePicker datePicker = null;
    private EditText country_editText = null;
    private Button play_button = null;

    private String firstName, lastName, countryOrArea, email, phone_number, password, gender = "";
    private int year, month, day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity_layout);

        initViews();

        //获取注册信息
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstName_editText.getText().toString();
                lastName = lastName_editText.getText().toString();
                email = email_editText.getText().toString();

                if (firstName.length() < 1 || lastName.length() < 1)
                    lastName_editText.setError("Cannot be NULL");
                else if (!datePicker.isSelected())
                    Toast.makeText(getApplicationContext(), "you should pick your birthday first", Toast.LENGTH_SHORT).show();
                else if (!male_radioButton.isSelected() && !female_radioButton.isSelected())
                    Toast.makeText(getApplicationContext(), "you should choose your gender first", Toast.LENGTH_SHORT).show();
                else if (!isEmail(email)) {
                    email_editText.setError("Not Valid");
                } else//注册信息完整
                {
                    firstName = firstName_editText.getText().toString();
                    lastName = lastName_editText.getText().toString();

                    year = datePicker.getYear();
                    month = datePicker.getMonth();
                    day = datePicker.getDayOfMonth();

                    countryOrArea = country_editText.getText().toString();
                    email = email_editText.getText().toString();

                    if (male_radioButton.isChecked())
                        gender = "男";
                    else if (female_radioButton.isChecked())
                        gender = "女";

                    //初始化avos
                    AVOSWrapper.init(SignUpActivity.this);

                    //set user info
                    AVUser user = new AVUser();
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setUsername(lastName + firstName);//todo 待定

                    user.put("birthday", year + "-" + month + "-" + day);
                    user.put("gender", gender);
                    user.put("phone_number", phone_number);
                    user.put("country", countryOrArea);

                    //sign up
                    AVOSWrapper.signUpInBackground(user, new SignUpCallbackWrapper() {
                        @Override
                        public void onSucceed() {
                            super.onSucceed();
                            //succeed
                            AVOSWrapper.logInInBackground(phone_number, password, new LogInCallbackWrapper() {

                                @Override
                                public void onSucceed(AVUser user) {
                                    super.onSucceed(user);
                                    // jump to main activity
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onFailed(AVException e) {
                                    super.onFailed(e);
                                    // notify with error
                                    promoteErrorMessage(getText(R.string.failed).toString());
                                }
                            });
                        }

                        @Override
                        public void onFailed(AVException e) {
                            super.onFailed(e);
                            //提示注册异常
                            promoteErrorMessage(getText(R.string.failed).toString());
                        }
                    });

                }
            }
        });


    }


    private void initViews() {

        firstName_editText = (EditText) findViewById(R.id.firstname_edittext);
        lastName_editText = (EditText) findViewById(R.id.lastname_edittext);
        email_editText = (EditText) findViewById(R.id.email_edittext);

        male_radioButton = (RadioButton) findViewById(R.id.male_radiobutton);
        male_radioButton.setSelected(true);
        female_radioButton = (RadioButton) findViewById(R.id.female_radiobutton);

        datePicker = (DatePicker) findViewById(R.id.datepicker);
        country_editText = (EditText) findViewById(R.id.country_edittext);
        play_button = (Button) findViewById(R.id.play_button);

        Intent intent = this.getIntent();
        phone_number = intent.getStringExtra("phoneNumber");
        password = intent.getStringExtra("password");
    }

    private boolean isEmail(String strEmail) {
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
}
