package app.view.friendspage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import app.view.login.R;

/**
 * 好友详情页
 * Created by lxs on 2014/8/3.
 */
public class FriendspageActivity extends Activity {

    private ImageView photo;
    private TextView name;
    private TextView distance;
    private Button bubble;
    private ImageView state;
    private LinearLayout album;
    private Button invite, talk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendspage_activity_layout);

        photo = (ImageView) findViewById(R.id.friendspage_photo);
        name = (TextView) findViewById(R.id.friendspage_name);
        distance = (TextView) findViewById(R.id.friendspage_distance);
        bubble = (Button) findViewById(R.id.friendspage_bubble_button);
        state = (ImageView) findViewById(R.id.friendspage_state);
        album = (LinearLayout) findViewById(R.id.friendspage_album);
        invite = (Button) findViewById(R.id.friendspage_invite_button);
        talk = (Button) findViewById(R.id.friendspage_talk_button);

        FriendspageListener listener = new FriendspageListener();
        photo.setOnClickListener(listener);
        bubble.setOnClickListener(listener);
        state.setOnClickListener(listener);
        album.setOnClickListener(listener);
        invite.setOnClickListener(listener);
        talk.setOnClickListener(listener);
    }

    //监听器
    class FriendspageListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.friendspage_photo:
                    //跳转到只显示头像图片的界面
                    //break;
                case R.id.friendspage_bubble_button:
                    //
                case R.id.friendspage_state:
                    //选择change状态
                case R.id.friendspage_album:
                    //跳转到该好友的活动界面
                case R.id.friendspage_invite_button:
                    //跳转到邀请界面
                case R.id.friendspage_talk_button:
                    //跳转到会话界面
            }

        }
    }
}

