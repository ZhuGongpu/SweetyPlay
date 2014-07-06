package app.view.main;

import android.app.Activity;
import android.os.Bundle;
import app.view.login.R;

/**
 * Created by zhugongpu on 14-7-6.
 * 主界面
 */
public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
    }
}
