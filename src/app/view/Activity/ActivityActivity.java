package app.view.Activity;

import android.app.Activity;
import android.os.Bundle;
import app.view.login.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 活动详情页
 * Created by lxs on 2014/8/4.
 */
public class ActivityActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_layout);
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        //测试代码
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("head", R.drawable.default_photo);
            list.add(item);
        }
        showOnLayout(list);

    }

    private void showOnLayout(ArrayList list) {
        //判断list长度
        for (int i = 0; i < 10; i++) {
            //从hashmap中取出值

        }

    }
}
