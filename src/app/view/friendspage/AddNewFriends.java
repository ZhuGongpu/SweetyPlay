package app.view.friendspage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import app.view.login.R;
import avos.AVOSWrapper;
import avos.callbackwrappers.FindCallbackWrapper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by lenovo on 2014/8/18.
 */
public class AddNewFriends extends Activity {

    private EditText searchFriend_EditText = null;
    private Button search_Button = null;
    private String userName = null;
    private String nickName = null;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.add_new_friend_layout);
        findView();

        userName = this.getIntent().getStringExtra("userName");
        nickName = this.getIntent().getStringExtra("nickName");

        search_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText_PhoneNumberOrNickName = searchFriend_EditText.getText().toString();
                //todo 查询
                AVQuery<AVUser> query = new AVQuery<AVUser>();
                query.whereEqualTo("nickName", searchText_PhoneNumberOrNickName);
                AVOSWrapper.query(query, new FindCallbackWrapper() {
                    @Override
                    public void onSucceed(List list) {

                    }

                    @Override
                    public void onFailed(AVException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void findView() {
        searchFriend_EditText = (EditText) findViewById(R.id.search_friend_EditText);
        search_Button = (Button) findViewById(R.id.search_Button);
    }
}
