package app.view.splash_screen;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import app.view.login.LoginActivity;
import app.view.login.R;
import app.view.main.MainActivity;
import avos.AVOSWrapper;

/**
 * Created by zhugongpu on 14-7-12.
 */
public class SplashScreenActivity extends Activity {

    private static final String TAG = "SplashScreenActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

        //跳转到login activity
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                //初始化AVOS
                AVOSWrapper.init(SplashScreenActivity.this);

                if (AVOSWrapper.getCurrentUser() != null)//不是第一次登陆
                {
                    //跳过login activity直接自动登录
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                } else {//第一次登录
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                finish();
                AVOSWrapper.logout();
            }
        }.execute();

    }
}
