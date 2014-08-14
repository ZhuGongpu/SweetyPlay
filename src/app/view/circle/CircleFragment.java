package app.view.circle;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.view.login.R;

/**
 * Created by Lewis on 8/5/14.
 */
public class CircleFragment extends Fragment {


    protected Context mContext;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //加载布局文件
        View view = inflater.inflate(R.layout.main_activity_layout, container, false);

        return view;
    }
}
