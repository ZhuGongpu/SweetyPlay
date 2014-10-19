package app.view.friendspage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import app.view.login.R;
import avos.AVOSWrapper;
import avos.callbackwrappers.FindCallbackWrapper;
import com.avos.avoscloud.*;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友详情页
 * Created by lxs on 2014/8/3.
 */
public class FriendsHomepageActivity extends Activity {

    protected AVUser user;
    private ImageView photo;
    private TextView name;
    private TextView distance;
    private Button bubble;
    private ImageView state;
    private LinearLayout album;
    private Button invite, talk;
    private String username, userId, installation_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_page_activity_layout);

        initView();
        getData();

    }


    protected void initView() {

        //get the data--user todo 这个嗲饭需要前面的数据
        Intent intent = getIntent();
        username = intent.getStringExtra("nickname");
        userId = intent.getStringExtra("userId");

        photo = (ImageView) findViewById(R.id.friendspage_photo);
        name = (TextView) findViewById(R.id.friendspage_name);
        distance = (TextView) findViewById(R.id.friendspage_distance);
        bubble = (Button) findViewById(R.id.friendspage_bubble_button);
        state = (ImageView) findViewById(R.id.friendspage_state);
        album = (LinearLayout) findViewById(R.id.friendspage_album);
        invite = (Button) findViewById(R.id.friendspage_invite_button);
        talk = (Button) findViewById(R.id.friendspage_talk_button);

        FriendsHomepageListener listener = new FriendsHomepageListener();
        photo.setOnClickListener(listener);
        bubble.setOnClickListener(listener);
        state.setOnClickListener(listener);
        album.setOnClickListener(listener);
        invite.setOnClickListener(listener);
        talk.setOnClickListener(listener);

    }

    //get the data from server
    public void getData() {
        AVOSWrapper.queryUser("username", username, new FindCallbackWrapper() {
            @Override
            public void onSucceed(List list) {
                if (list.size() > 0) {
                    user = (AVUser) list.get(0);
                    setView();
                }

            }

            @Override
            public void onFailed(AVException e) {
                Toast.makeText(getApplication(), "无法获取信息，请联系我们", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //set the data of content
    private void setView() {
        name.setText(user.get("nickname").toString());
        int photoWidth = photo.getWidth();
        int photoHeight = photo.getHeight();
        Picasso.with(this).load(AVOSWrapper.getUserAvatarUrl(
                user, photoWidth, photoHeight)).error(R.drawable.default_photo);

        installation_id = user.get("installation_id").toString();


    }

    private void sendBubble() {
        AVPush push = new AVPush();
        push.setQuery(AVInstallation.getQuery().whereEqualTo("installation_id", installation_id));
        push.setChannel("bubble");
        //set Data
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("action", "com.avos.UPDATE_STATUS");
        map.put("message", username + " Bubble了你一下!");
        map.put("userId", userId + "");
        map.put("nickname", username);
        push.setData(map);

        //push
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(AVException e) {
                String sendResult;

                if (e == null) {
                    sendResult = "send Successfully";
                } else {
                    sendResult = "send Failed";
                }

                Toast.makeText(FriendsHomepageActivity.this, sendResult, Toast.LENGTH_LONG).show();

            }
        });


    }

    //监听器
    class FriendsHomepageListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.friendspage_photo:
                    //跳转到只显示头像图片的界面
                    //break;
                case R.id.friendspage_bubble_button:
                    //push a message to the user
                    sendBubble();

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
