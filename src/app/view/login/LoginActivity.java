package app.view.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import app.util.PasswordFormatValidator;
import app.util.PhoneNumberFormatValidator;
import app.view.main.MainActivity;
import app.view.signup.SignUpActivity;
import avos.AVOSWrapper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;


/**
 * 登录界面
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    /**
     * UI Elements
     */
    private ImageButton loginButton = null;
    private ImageButton signupButton = null;
    private EditText phoneNumberEditText = null;
    private EditText passwordEditText = null;
    private EditText confirmPasswordEditText = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);

        initViews();
    }

    /**
     * 初始化界面, init all the UI elements
     */
    private void initViews() {

        loginButton = (ImageButton) findViewById(R.id.login_button);
        signupButton = (ImageButton) findViewById(R.id.signup_button);

        //set listeners
        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);

        //默认是登录
        findViewById(R.id.login_button_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.signup_button_layout).setVisibility(View.GONE);
        phoneNumberEditText = (EditText) findViewById(R.id.login_button_layout_phone_number);
        passwordEditText = (EditText) findViewById(R.id.login_button_layout_password);
    }

    private void initSignUpLayout() {
        findViewById(R.id.login_button_layout).setVisibility(View.GONE);
        findViewById(R.id.signup_button_layout).setVisibility(View.VISIBLE);

        phoneNumberEditText = (EditText) findViewById(R.id.login_button_layout_phone_number);
        passwordEditText = (EditText) findViewById(R.id.signup_button_layout_password);
        confirmPasswordEditText = (EditText) findViewById(R.id.signup_button_layout_confirm_password);

        //TODO add animation
    }

    /**
     * 处理点击事件
     *
     * @param view 点击的控件
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {//login

            if (findViewById(R.id.login_button_layout).getVisibility() == View.VISIBLE) {//用于防止 点完 sign up button 之后再点login 的情况

                //TODO validate first
                final String phone_number = phoneNumberEditText.getText() + "";
                final String password = passwordEditText.getText() + "";

                if (PasswordFormatValidator.isLegalPassword(password) && PhoneNumberFormatValidator.isLegalPhoneNumber(phone_number)) {//允许登录

                    AVOSWrapper.init(LoginActivity.this);//初始化

                    AVOSWrapper.logInInBackground(phone_number, password, new LogInCallback() {
                        @Override
                        public void done(AVUser user, AVException e) {
                            if (user != null) {//登陆成功
                                // jump to main activity
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else { //登录失败
                                // notify with error
                                promoteErrorMessage(getText(R.string.failed).toString());
                            }
                        }
                    });

                } else {// 不允许登录，prompt error
                    if (!PasswordFormatValidator.isLegalPassword(password)) {
                        passwordEditText.setError(getText(R.string.password_is_not_legal));
                    }
                    if (!PhoneNumberFormatValidator.isLegalPhoneNumber(phone_number)) {
                        if (phoneNumberEditText != null)
                            phoneNumberEditText.setError(getText(R.string.phone_number_is_not_legal));
                    }
                }

            } else //显示 login layout
            {
                findViewById(R.id.signup_button_layout).setVisibility(View.GONE);
                findViewById(R.id.login_button_layout).setVisibility(View.VISIBLE);
                //TODO add animation
            }
        } else if (view.getId() == R.id.signup_button) {
            //TODO sign up : jump to SignUpActivity

            if (findViewById(R.id.signup_button_layout).getVisibility() == View.VISIBLE) {
                String phone_number = phoneNumberEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (confirmPassword.equals(password)) {
                    if (PasswordFormatValidator.isLegalPassword(password) && PhoneNumberFormatValidator.isLegalPhoneNumber(phone_number)) {
                        //将以填写的用户信息传入sign up activity
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

                        //传入参数
                        intent.putExtra("phoneNumber", phone_number);
                        intent.putExtra("password", password);

                        startActivity(intent);
                        finish();
                    } else {// prompt error

                        if (!PasswordFormatValidator.isLegalPassword(password))
                            passwordEditText.setError(getText(R.string.password_is_not_legal));

                        if (!PhoneNumberFormatValidator.isLegalPhoneNumber(phone_number))
                            phoneNumberEditText.setError(getText(R.string.phone_number_is_not_legal));
                    }
                } else {
                    confirmPasswordEditText.setError(getText(R.string.passwords_do_not_match));
                }

            } else {//初次点击 sign up button，比如从login button切换到sign up
                initSignUpLayout();
            }
        }
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
