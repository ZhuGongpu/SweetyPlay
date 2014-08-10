package app.view.friendspage;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import app.util.PingYinUtil;
import app.util.PinyinComparator;
import app.util.SideBar;
import app.view.login.R;
import avos.AVOSWrapper;
import avos.callbackwrappers.FindCallbackWrapper;
import com.avos.avoscloud.AVException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lewis on 8/5/14.
 */
public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";
    /**
     * 获取库Phone表字段*
     */
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    /**
     * 联系人显示名称*
     */
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**
     * 电话号码*
     */
    private static final int PHONES_NUMBER_INDEX = 1;
    protected View view;
    ArrayList<HashMap<String, Object>> data;
    private ListView lvContact;
    private SideBar indexBar;
    private WindowManager mWindowManager;
    private TextView mDialogText;
    /**
     * 联系人名称*
     */
    private ArrayList<String> mContactsName = new ArrayList<String>();

    /**
     * 联系人号码*
     */
    private ArrayList<String> mContactsNumber = new ArrayList<String>();

    private String[] nickNames_phoneNumbers, nickNames, phoneNumbers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friendlist, container, false);

        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        getPhoneContacts();//获得手机通讯录联系人
        //getSIMContacts();//获取SIM卡联系人
        data = getData();//获得数据

        nickNames = new String[data.size()];
        phoneNumbers = new String[data.size()];
        nickNames_phoneNumbers = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            nickNames_phoneNumbers[i] = data.get(i).get("nickName&phoneNumber").toString();
        }

        findView();

        return view;
    }

    private void findView() {
        lvContact = (ListView) view.findViewById(R.id.lvContact);
        lvContact.setAdapter(new ContactAdapter(getActivity()));//设置适配器
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0 || i == 1 || i == 2) {
                    Toast.makeText(getActivity().getApplicationContext(), "i am " + i, Toast.LENGTH_SHORT).show();
                } else {
                    /*
                    AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
                    query.whereEqualTo("username", "12345678908");
                    query.findInBackground(new FindCallback<AVObject>() {
                        public void done(List<AVObject> avObjects, AVException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), "成功查询到 " + avObjects.size() + " 条符合条件的数据", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "失败!查询错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    AVQuery<AVObject> query = new AVQuery<AVObject>("_User");
                    query.whereEqualTo("username", "12345678908");
                    try {
                        List<AVObject> avObjects = query.find();
                    } catch (AVException e) {
                        Toast.makeText(getActivity(),"失败",Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getActivity(), "成功查询", Toast.LENGTH_SHORT).show();
                    */
                    AVOSWrapper.init(getActivity());

                    AVOSWrapper.queryUser("username", "Admin", new FindCallbackWrapper() {
                        @Override
                        public void onSucceed(List list) {
                            super.onSucceed(list);
//todo
                            Log.e(TAG, "result : " + (list.size() > 0));
                        }

                        @Override
                        public void onFailed(AVException e) {
                            super.onFailed(e);

                            e.printStackTrace();
                        }
                    });
                }
            }
        });

        indexBar = (SideBar) view.findViewById(R.id.sideBar);
        indexBar.setListView(lvContact);
        mDialogText = (TextView) LayoutInflater.from(getActivity()).inflate(
                R.layout.list_position, null);
        mDialogText.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(mDialogText, lp);
        indexBar.setTextView(mDialogText);
    }

    /**
     * 获取好友数据Item*
     */
    private ArrayList<HashMap<String, Object>> getData() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        HashMap<String, Object> map;

        for (int i = 0; i < mContactsName.size(); i++) {
            map = new HashMap<String, Object>();
            map.put("nickName&phoneNumber", mContactsName.get(i) + " " + mContactsNumber.get(i));
            //map.put("img", R.drawable.doge);
            list.add(map);
        }
        return list;
    }
    /**
     * 昵称
     */

    /**
     * 得到手机SIM卡联系人人信息*
     */
    private void getSIMContacts() {
        ContentResolver resolver = getActivity().getContentResolver();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);

        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);

                //Sim卡中没有联系人头像

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
            }

            phoneCursor.close();
        }
    }

    /**
     * 得到手机通讯录联系人信息*
     */
    private void getPhoneContacts() {
        ContentResolver resolver = getActivity().getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);


        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者    为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                mContactsName.add(contactName);
                mContactsNumber.add(phoneNumber);
            }

            phoneCursor.close();
        }
    }

    class ContactAdapter extends BaseAdapter implements SectionIndexer {
        private Context mContext;
        private String[] mNicks;

        @SuppressWarnings("unchecked")
        public ContactAdapter(Context mContext) {
            this.mContext = mContext;
            this.mNicks = nickNames_phoneNumbers;
            // 排序(实现了中英文混排)
            Arrays.sort(mNicks, new PinyinComparator());
        }

        @Override
        public int getCount() {
            return mNicks.length;
        }

        @Override
        public Object getItem(int position) {
            return mNicks[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.contact_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvCatalog = (TextView) convertView
                        .findViewById(R.id.contactitem_catalog);
                viewHolder.ivAvatar = (ImageView) convertView
                        .findViewById(R.id.contactitem_avatar_iv);
                viewHolder.tvNick = (TextView) convertView
                        .findViewById(R.id.contactitem_nick);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                viewHolder.tvCatalog.setVisibility(View.GONE);
                viewHolder.ivAvatar.setImageResource(R.drawable.invite_icon);
                viewHolder.tvNick.setText("Invite friends by play ID");
            } else if (position == 1) {
                viewHolder.tvCatalog.setVisibility(View.GONE);
                viewHolder.ivAvatar.setImageResource(R.drawable.invite_icon);
                viewHolder.tvNick.setText("Invite friends by PhoneNumber");
            } else if (position == 2) {
                viewHolder.tvCatalog.setVisibility(View.GONE);
                viewHolder.ivAvatar.setImageResource(R.drawable.invite_icon);
                viewHolder.tvNick.setText("New Friends");
            } else {
                final String nickName_phoneNumber = mNicks[position - 3];
                String catalog = PingYinUtil.converterToFirstSpell(nickName_phoneNumber)
                        .substring(0, 1);
                if (position == 3) {
                    viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                    viewHolder.tvCatalog.setText(catalog);
                } else {
                    String lastCatalog = PingYinUtil.converterToFirstSpell(
                            mNicks[position - 4]).substring(0, 1);
                    if (catalog.equals(lastCatalog)) {
                        viewHolder.tvCatalog.setVisibility(View.GONE);
                    } else {
                        viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                        viewHolder.tvCatalog.setText(catalog);
                    }
                }
                viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);
                String[] tmp = nickName_phoneNumber.split(" ");
                nickNames[position - 3] = tmp[0];
                phoneNumbers[position - 3] = tmp[1];
                viewHolder.tvNick.setText(nickNames[position - 3]);
            }
            return convertView;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 0; i < mNicks.length; i++) {
                String l = PingYinUtil.converterToFirstSpell(mNicks[i])
                        .substring(0, 1);
                char firstChar = l.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            return null;
        }

        class ViewHolder {
            TextView tvCatalog;// 目录
            ImageView ivAvatar;// 头像
            TextView tvNick;// 昵称
        }
    }
}
