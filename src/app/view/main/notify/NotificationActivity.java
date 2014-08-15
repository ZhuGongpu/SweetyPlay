package app.view.main.notify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import app.view.login.R;
import avos.models.BubbleEntity;
import avos.models.PlayEntity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

/**
 * Created by Lewis on 8/9/14.
 */
public class NotificationActivity extends Activity {

    protected ListView notification_listView;

    private List<PlayEntity> playLists;
    private List<BubbleEntity> bubbleLists;

    private DbUtils mDbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_layout);

        //init the dataBase
        mDbUtil = DbUtils.create(this);

        initData();


        notification_listView = (ListView) findViewById(R.id.notification_listView);
        notification_listView.setAdapter(new NotificationAdapter(this));


    }


    private void initData() {
        Intent intent = getIntent();
        int type = intent.getExtras().getInt("contentType");

        if (type == 1) {
            //get the data from receiver
            String userId = intent.getExtras().getString("userId");
            String message = intent.getExtras().getString("message");
            String nickName = intent.getExtras().getString("nickName");

            //create a bubble and add it to sqlite
            BubbleEntity bubble = new BubbleEntity(nickName, message, userId);

            try {

                mDbUtil.save(bubble);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        //get the data from database
        try {
            bubbleLists = mDbUtil.findAll(BubbleEntity.class);


        } catch (DbException e) {
            e.printStackTrace();
        }

    }


    private class NotificationAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public NotificationAdapter(Context mContext) {
            super();
            this.mContext = mContext;

            mLayoutInflater = LayoutInflater.from(mContext);


        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;

            if (convertView == null) {
                view = mLayoutInflater.inflate(R.layout.notification_item_layout, null);

                viewHolder = new ViewHolder();

                //将找到的控件放入到控件Holder里面
                viewHolder.activity_photo_imageView = (ImageView) view.findViewById(
                        R.id.activity_photo_imageView);

                viewHolder.activity_title_textView = (TextView) view.findViewById(
                        R.id.activity_title_textView);

                viewHolder.join_button = (ImageView) view.findViewById(R.id.join_button);

                viewHolder.left_time_textView = (TextView) view.findViewById(
                        R.id.left_time_textView);

                viewHolder.notify_user_photo = (ImageView) view.findViewById(
                        R.id.notify_user_photo);


                view.setTag(viewHolder);


            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            //todo 对相关控件进行数据加载工作

            return view;
        }


        //用来存储控件的类
        class ViewHolder {
            ImageView activity_photo_imageView;    //活动照片
            TextView activity_title_textView;      //活动名称
            TextView left_time_textView;           //活动剩余时间
            ImageView join_button;                 //加入活动按钮
            ImageView notify_user_photo;           //邀请人的头像
        }


    }


}
