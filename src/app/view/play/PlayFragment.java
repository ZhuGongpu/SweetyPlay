package app.view.play;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.view.activity.PlayDetailActivity;
import app.view.login.R;
import app.view.play.adapter.PlayListAdapter;
import avos.AVOSWrapper;
import avos.callbackwrappers.FindCallbackWrapper;
import avos.models.PlayEntity;
import com.avos.avoscloud.AVException;
import freeflow.core.AbsLayoutContainer;
import freeflow.core.FreeFlowContainer;
import freeflow.core.FreeFlowItem;
import freeflow.layouts.FreeFlowLayout;
import freeflow.layouts.HLayout;
import freeflow.layouts.VGridLayout;
import freeflow.layouts.VLayout;

import java.util.List;

/**
 * Created by Lewis on 8/5/14.
 */
public class PlayFragment extends Fragment {

    public static final String TAG = "PlayFragment";
    protected View view;
    PlayListAdapter adapter;
    FreeFlowLayout[] layouts;
    int currLayoutIndex = 0;
    private FreeFlowContainer container;
    private VGridLayout grid;
    private PlayListLayout custom;

    private int skip = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.playlist_layout, container, false);
        init(getActivity());

        return view;
    }

    /**
     * 初始化 Fragment
     *
     * @param activity
     */
    private void init(Activity activity) {
        container = (FreeFlowContainer) view.findViewById(R.id.play_list_container);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Our new layout
        custom = new PlayListLayout();

        //Grid Layout
        grid = new VGridLayout();
        VGridLayout.LayoutParams params = new VGridLayout.LayoutParams(size.x / 2, size.x / 2);
        grid.setLayoutParams(params);

        //Vertical Layout
        VLayout vlayout = new VLayout();
        VLayout.LayoutParams params2 = new VLayout.LayoutParams(size.x);
        vlayout.setLayoutParams(params2);

        //HLayout
        HLayout hlayout = new HLayout();
        hlayout.setLayoutParams(new HLayout.LayoutParams(size.x));


        layouts = new FreeFlowLayout[]{custom, grid, vlayout, hlayout};

        adapter = new PlayListAdapter(activity);

        container.setLayout(layouts[currLayoutIndex]);
        container.setAdapter(adapter);

        loadMore();
    }

    //todo
    public void onDataLoaded(List<PlayEntity> playList) {

        adapter.update(playList);
        container.dataInvalidated();
        container.setOnItemClickListener(new AbsLayoutContainer.OnItemClickListener() {
            @Override
            public void onItemClick(AbsLayoutContainer parent, FreeFlowItem proxy) {
                //todo 响应点击事件
                Log.e(TAG, "Click : " + ((TextView) proxy.view.findViewById(R.id.title)).getText() + "  id : " + ((PlayEntity) proxy.data).getObjectId());

                Intent intent = new Intent(getActivity(), PlayDetailActivity.class);
                intent.putExtra("PlayID", ((PlayEntity) proxy.data).getObjectId());
                startActivity(intent);
            }
        });

        container.addScrollListener(new FreeFlowContainer.OnScrollListener() {

            @Override
            public void onScroll(FreeFlowContainer container) {
                Log.d(TAG, "scroll percent " + container.getScrollPercentY());

                if (container.getScrollPercentY() >= 1) {
                    //加载更多
                    loadMore();
                } else if (container.getScrollPercentY() <= 0) {
                    //刷新
                    refresh();
                }

            }
        });
    }

    /**
     * 刷新界面
     */
    private void refresh() {
        adapter.clearData();
        skip = 0;
        loadMore();
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        AVOSWrapper.queryPlays(skip, 30, new FindCallbackWrapper<PlayEntity>() {
            @Override
            public void onSucceed(List<PlayEntity> list) {
                Log.e(TAG, "onSucceed : " + list.size());
                skip += list.size();
                onDataLoaded(list);
            }

            @Override
            public void onFailed(AVException e) {
                //TODO notify users with errors
            }
        });

    }

    /**
     * 更改布局
     */
    public void changeLayout() {
        currLayoutIndex++;
        if (currLayoutIndex == layouts.length) {
            currLayoutIndex = 0;
        }
        container.setLayout(layouts[currLayoutIndex]);
    }

}
