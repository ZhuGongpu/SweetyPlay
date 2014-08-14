package app.view.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import app.view.login.R;
import avos.AVOSWrapper;
import avos.callbackwrappers.FindCallbackWrapper;
import avos.models.PlayEntity;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.squareup.picasso.Picasso;
import freeflow.core.FreeFlowContainer;
import freeflow.core.FreeFlowItem;
import freeflow.core.Section;
import freeflow.core.SectionedAdapter;
import freeflow.layouts.HLayout;

import java.util.List;

/**
 * 活动详情页
 * <p/>
 * 需要以putString的形式传入PlayID, key = "PlayID"
 * <p/>
 * Created by lxs on 2014/8/4.
 */
public class PlayDetailActivity extends Activity {

    private final static String TAG = "PlayDetailActivity";

    /**
     * UI Elements
     */
    private TextView playTitle = null;
    private ImageView playPhoto = null;
    private Button joinButton = null;
    private TextView joinerNumberTextView = null;
    private FreeFlowContainer joinerListContainer = null;
    private TextView playDateTextView = null;
    private TextView playLocationTextView = null;
    private TextView playDescriptionTextView = null;

    private PlayEntity currentPlay = null;

    private JoinerListAdapter joinerListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_detail_layout);

        String playID = getIntent().getStringExtra("PlayID");

        initViews();
        Log.e("PlayDetailActivity", playID);
        AVOSWrapper.queryPlay(playID, new FindCallbackWrapper<PlayEntity>() {
            @Override
            public void onSucceed(List<PlayEntity> list) {
                if (list.size() > 0) {
                    currentPlay = list.get(0);
                    setViews();
                    Log.e(TAG, list.size() + "");

                }

                Log.e(TAG, "on succeed");
            }

            @Override
            public void onFailed(AVException e) {
                /*
                Log.e("PlayDetailActivity",e.getLocalizedMessage());
                Toast.makeText(PlayDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                startActivity(new Intent(PlayDetailActivity.this, MainActivity.class));
                finish();
                */
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        playTitle = (TextView) findViewById(R.id.play_detail_layout_play_title);
        playPhoto = (ImageView) findViewById(R.id.play_detail_layout_play_photo);
        joinButton = (Button) findViewById(R.id.play_detail_layout_join_button);
        joinerNumberTextView = (TextView) findViewById(R.id.play_detail_layout_joiner_number_text_view);
        joinerListContainer = (FreeFlowContainer) findViewById(R.id.play_detail_layout_joiner_list_container);
        playDateTextView = (TextView) findViewById(R.id.play_detail_layout_play_date_textview);
        playLocationTextView = (TextView) findViewById(R.id.play_detail_layout_play_location_textview);
        playDescriptionTextView = (TextView) findViewById(R.id.play_detail_layout_play_description);

        // join the play
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPlay != null) {
                    joinButton.setText("已加入");
                    joinButton.setEnabled(false);
                    //join
                    currentPlay.getParticipationList().add(AVOSWrapper.getCurrentUser());
                    currentPlay.setCurrentNumber(currentPlay.getCurrentNumber() + 1);
                    loadJoinerList();
                }
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HLayout hlayout = new HLayout();
        hlayout.setLayoutParams(new HLayout.LayoutParams(size.x));
        joinerListContainer.setLayout(hlayout);

        joinerListAdapter = new JoinerListAdapter(this);
        joinerListContainer.setAdapter(joinerListAdapter);
    }

    /**
     * 设置Views Content
     */
    private void setViews() {
        playTitle.setText(currentPlay.getTitle());
        Picasso.with(this).load(currentPlay.getThumbnailUrl()).error(R.drawable.ic_launcher).into(playPhoto);//若加载失败，设置为默认图片
        joinerNumberTextView.setText(String.format("％s人参加", currentPlay.getCurrentNumber()));
        playDateTextView.setText(currentPlay.getPlayDate().toString());//TODO format
        playLocationTextView.setText(currentPlay.getPlace().toString());//TODO convert
        playDescriptionTextView.setText(currentPlay.getDescription());

        //获取joiner列表
        loadJoinerList();
    }

    private void loadJoinerList() {
        if (currentPlay != null) {
            AVOSWrapper.query(currentPlay.getParticipationList().getQuery(), new FindCallbackWrapper<AVUser>() {
                @Override
                public void onSucceed(List<AVUser> list) {
                    if (list.size() > 0) {
                        joinerListAdapter.clearData();
                        joinerListAdapter.update(list);
                        joinerListContainer.dataInvalidated();
                    }
                }

                @Override
                public void onFailed(AVException e) {
                    Toast.makeText(PlayDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();


                }
            });
        }
    }

    private class JoinerListAdapter implements SectionedAdapter {

        public static final String TAG = "JoinerListAdapter";

        private Context context;
        private Section section;

        private int[] colors = new int[]{0xcc152431, 0xff264C58, 0xffF5C543,
                0xffE0952C, 0xff9A5325, 0xaaE0952C, 0xaa9A5325, 0xaa152431,
                0xaa264C58, 0xaaF5C543, 0x44264C58, 0x44F5C543, 0x44152431};

        private boolean hideImages = false;

        public JoinerListAdapter(Context context) {
            this.context = context;
            section = new Section();
        }

        /**
         * 清除所有数据
         */
        public void clearData() {
            section.getData().clear();
        }

        public void update(List list) {
            for (Object object : list) {
                section.getData().add(object);
            }
        }

        @Override
        public long getItemId(int section, int position) {
            return section * 1000 + position;
        }

        @Override
        public View getItemView(int sectionIndex, int position, View convertView,
                                ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.playlist_item_layout, parent, false);
            }

            ImageView image = (ImageView) convertView.findViewById(R.id.pic);
            TextView title = (TextView) convertView.findViewById(R.id.title);

            if (hideImages) {
                int idx = position % colors.length;
                image.setBackgroundColor(colors[idx]);

            } else {
                AVUser user = (AVUser) this.section.getData().get(position);
                title.setText(user.getString("nickName"));
                Picasso.with(context)
                        .load(AVOSWrapper.getUserAvatarUrl(user, 50, 50))
                        .into(image);
            }

            return convertView;
        }

        @Override
        public View getHeaderViewForSection(int section, View convertView,
                                            ViewGroup parent) {
            return null;
        }

        @Override
        public int getNumberOfSections() {
            if (section.getData().size() == 0) return 0;
            return 1;
        }

        @Override
        public Section getSection(int index) {
            return section;
        }

        @Override
        public Class[] getViewTypes() {
            return new Class[]{LinearLayout.class};
        }

        @Override
        public Class getViewType(FreeFlowItem proxy) {
            return LinearLayout.class;
        }

        @Override
        public boolean shouldDisplaySectionHeaders() {
            return false;
        }
    }
}
