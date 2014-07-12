package app.view.signup;

import android.app.Activity;
import android.os.Bundle;
import app.view.login.R;

/**
 * sign up界面
 * 进入此界面之前，已点击loginActivity 中的sign up按钮，并已填写合法的phone number & password
 * 使用时，需将填写好的信息以 phoneNumber 和 password 的形式传入，用于进一步处理（完善信息）
 * Created by zhugongpu on 14-7-7.
 */
public class SignUpActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity_layout);

        //TODO 接收传入的user信息,并初始化界面 和 初始化 avos

    }


}
