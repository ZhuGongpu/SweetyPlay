package app.view.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


/**
 * 登录界面
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    /**
     * UI Elements
     */
    private ImageButton loginButton = null;
    private ImageButton signupButton = null;

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

    }

    /**
     * 处理点击事件
     *
     * @param view 点击的控件
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_button) {
            //TODO login and jump to MainActivity

        } else if (view.getId() == R.id.signup_button) {
            //TODO sign up : jump to SignupActivity

        }
    }
}
