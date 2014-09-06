package app.view.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import app.DemoApplication;
import app.view.signup.SignUpActivity;
import helper.SweetyPlayHelper;
import utils.CommonUtils;
import utils.PasswordFormatValidator;
import utils.PhoneNumberFormatValidator;
import utils.PhoneNumberUtility;


/**
 * 登录界面
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    /**
     * UI Elements
     */
    private ImageButton loginButton = null;
    private ImageButton signupButton = null;
    private EditText phoneNumberEditText = null;
    private EditText passwordEditText = null;
    private EditText confirmPasswordEditText = null;
    private boolean progressShow = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如果用户名密码都有，直接进入主页面
        if (DemoApplication.getInstance().getUsername() != null && DemoApplication.getInstance().getPassword() != null) {
            SweetyPlayHelper.loginAsync(this, DemoApplication.getInstance().getUsername(), DemoApplication.getInstance().getPassword(), "正在登录...");
        }

        setContentView(R.layout.login_activity_layout);

        initViews();
    }

    /**
     * 初始化界面, init all the UI elements
     * 显示login layout
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

        //add animation
        findViewById(R.id.login_button_layout).startAnimation(AnimationUtils.loadAnimation(this, R.anim.drop_down));

        //设置phoneNumber为本机号码
        phoneNumberEditText.setText(PhoneNumberUtility.getPhoneNumber(this) + "");
        phoneNumberEditText.setSelection(phoneNumberEditText.getText().length());

    }

    /**
     * 显示sign up layout
     */
    private void initSignUpLayout() {

        //设置login layout不可见
        findViewById(R.id.login_button_layout).setVisibility(View.GONE);
        findViewById(R.id.signup_button_layout).setVisibility(View.VISIBLE);

        //add animation
        Animation dropDownAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_down);
        findViewById(R.id.signup_button_layout).startAnimation(dropDownAnimation);

        //获取控件
        phoneNumberEditText = (EditText) findViewById(R.id.signup_button_layout_phone_number);
        passwordEditText = (EditText) findViewById(R.id.signup_button_layout_password);
        confirmPasswordEditText = (EditText) findViewById(R.id.signup_button_layout_confirm_password);

        //设置phoneNumber为本机号码
        phoneNumberEditText.setText(PhoneNumberUtility.getPhoneNumber(this) + "");
        phoneNumberEditText.setSelection(phoneNumberEditText.getText().length());

    }

    /**
     * 处理点击事件
     *
     * @param view 点击的控件
     */
    @Override
    public void onClick(View view) {

        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }


        if (view.getId() == R.id.login_button) {//login

            if (findViewById(R.id.login_button_layout).getVisibility() == View.VISIBLE) {//用于防止 点完 sign up button 之后再点login 的情况

                Log.e(TAG, "login clicked");

                final String phone_number = phoneNumberEditText.getText() + "";
                final String password = passwordEditText.getText() + "";

                if (PasswordFormatValidator.isLegalPassword(password) && PhoneNumberFormatValidator.isLegalPhoneNumber(phone_number)) {//允许登录
                    //Log.e(TAG, "Login Clicked");

                    SweetyPlayHelper.loginAsync(this, phone_number, password, "正在登录...");

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

                //add animation
                findViewById(R.id.login_button_layout).startAnimation(AnimationUtils.loadAnimation(this, R.anim.drop_down));
            }
        } else if (view.getId() == R.id.signup_button) {
            //sign up : jump to SignUpActivity

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
