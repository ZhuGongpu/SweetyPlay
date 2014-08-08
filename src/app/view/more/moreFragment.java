package app.view.more;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.view.login.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Lewis on 8/5/14.
 */
public class moreFragment extends Fragment {

    protected View view;

    //moreFragment的控件
    protected ImageView userPhoto_imageView;
    protected RelativeLayout turnHomePage_Relativelayout;
    protected TextView nickName_textView;
    protected TextView university_textView;
    protected TextView birthdate_textView;
    protected RelativeLayout setting_RelativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.more_activity_layout, container, false);

        initView();

        return view;
    }

    /**
     * 初始化控件
     */
    protected void initView() {

        //find viewId
        userPhoto_imageView = (ImageView) view.findViewById(R.id.more_user_photo);
        turnHomePage_Relativelayout = (RelativeLayout) view.findViewById
                (R.id.turnHomePage_relativeLayout);

        nickName_textView = (TextView) view.findViewById(R.id.nickname_textView);
        university_textView = (TextView) view.findViewById(R.id.university_textView);
        birthdate_textView = (TextView) view.findViewById(R.id.birthdate_textView);
        setting_RelativeLayout = (RelativeLayout) view.findViewById(R.id.setting_RelativeLayout);


        //set listener

        userPhoto_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo 更换头像

            }
        });

        turnHomePage_Relativelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 跳转到用户的个人主页

            }
        });


        setting_RelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 跳转到设置页面

            }
        });


    }


}
