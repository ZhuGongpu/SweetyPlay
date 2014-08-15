package app.view.main;

import android.os.Bundle;
import app.baseActivity.IndicatorFragmentActivity;
import app.view.circle.CircleFragment;
import app.view.friendspage.FriendsFragment;
import app.view.more.moreFragment;
import app.view.play_list.PlayListFragment;

import java.util.List;

/**
 * Created by zhugongpu on 14-7-6.
 * 主界面
 */
public class MainActivity extends IndicatorFragmentActivity {


    public static final int PLAY_FRAGMENT = 0;
    public static final int CIRCLE_FRAGMENT = 1;
    public static final int FRIENDS_FRAGMENT = 2;
    public static final int MORE_FRAGMENT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {

        /**
         * 加载四个标签页
         */
        tabs.add(new TabInfo(PLAY_FRAGMENT, "playlist", PlayListFragment.class));
        tabs.add(new TabInfo(CIRCLE_FRAGMENT, "circle", CircleFragment.class));
        tabs.add(new TabInfo(FRIENDS_FRAGMENT, "friends", FriendsFragment.class));
        tabs.add(new TabInfo(MORE_FRAGMENT, "more", moreFragment.class));

        return PLAY_FRAGMENT;
    }
}
