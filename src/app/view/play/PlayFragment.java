package app.view.play;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.view.login.R;

/**
 * Created by Lewis on 8/5/14.
 */
public class PlayFragment extends Fragment {

    protected View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.main_activity_layout,container,false);

       return view;
    }
}
