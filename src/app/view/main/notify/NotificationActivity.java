package app.view.main.notify;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import app.view.login.R;

/**
 * Created by Lewis on 8/9/14.
 */
public class NotificationActivity extends Activity {

    protected ListView notification_listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_item_layout);

        notification_listView = (ListView)findViewById(R.id.notification_listView);


    }

    private class NotificationAdapter extends BaseAdapter{

        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public NotificationAdapter(Context mContext) {
            super();
            this.mContext = mContext;

            mLayoutInflater= LayoutInflater.from(mContext);


        }

        @Override
        public int getCount() {
            return 0;
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

            return null;
        }
    }


}
