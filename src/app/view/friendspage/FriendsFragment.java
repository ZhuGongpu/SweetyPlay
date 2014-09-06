package app.view.friendspage;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import app.util.AVUserComparator;
import app.util.PingYinUtil;
import app.util.SideBar;
import app.view.login.R;
import avos.AVOSWrapper;
import avos.callbackwrappers.FindCallbackWrapper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final int UPDATE_SIGNAL = 1;
    public static List<AVUser> avUsersList = new ArrayList<AVUser>();//AVUser好友列表
    protected View view;

    private ListView lvContact;//好友listview
    private ContactAdapter contactAdapter = null;//适配器

    private SideBar indexBar;
    private WindowManager mWindowManager;
    private TextView mDialogText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friendlist, container, false);

        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        findView();

        AVOSWrapper.getFriendList(AVUser.getCurrentUser(), new FindCallbackWrapper<AVUser>() {
            @Override
            public void onSucceed(List<AVUser> list) {
                avUsersList = list;
                /** 排序 **/
                Collections.sort(avUsersList, new AVUserComparator());
                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(AVException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        });

        return view;
    }

    private void findView() {
        lvContact = (ListView) view.findViewById(R.id.lvContact);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {//按活动ID邀请好友
                    //todo 按活动ID邀请好友
                } else if (i == 1) {//按电话号码邀请好友
                    Intent intent = new Intent(getActivity(), AddNewFriends.class);
                    intent.putExtra("nickName", AVOSWrapper.getCurrentUser().get("nickName").toString());
                    intent.putExtra("userName", AVOSWrapper.getCurrentUser().getUsername());
                    getActivity().startActivity(intent);
                    //todo 按电话号码邀请好友
                } else if (i == 2) {//新的好友
                    //todo 添加新的好友
                } else {
                    String nickName = avUsersList.get(i - 3).get("nickName").toString();
                    String userID = avUsersList.get(i - 3).getObjectId();
                    Intent intent = new Intent(getActivity(), FriendsHomepageActivity.class);
                    intent.putExtra("nickName", nickName);
                    intent.putExtra("userID", userID);
                    getActivity().startActivity(intent);
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

        contactAdapter = new ContactAdapter(getActivity());//新建适配器

        lvContact.setAdapter(contactAdapter);//设置适配器

        contactAdapter.notifyDataSetChanged();
    }

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
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                contactName.replace(" ", "");//去除空格

                //Sim卡中没有联系人头像
                Contact contact = new Contact();
                contact.name = contactName;
                contact.phoneNumber = phoneNumber;

//                if (contact.hasSignedUp())
//                    contacts.add(contact);
            }

            phoneCursor.close();
        }
    }

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
                contactName.replace(" ", "");//去除空格
                //TODO 获取头像

                Contact contact = new Contact();
                contact.name = contactName;
                contact.phoneNumber = phoneNumber;

//                if (contact.hasSignedUp())
//                    contacts.add(contact);
            }

            phoneCursor.close();


        }
    }

    class ContactAdapter extends BaseAdapter implements SectionIndexer {
        private Context mContext;


        @SuppressWarnings("unchecked")
        public ContactAdapter(Context mContext) {
            this.mContext = mContext;

        }

        @Override
        public int getCount() {
            //return contacts.size();
            return avUsersList.size() + 3;
        }

        @Override
        public Object getItem(int position) {
//            return contacts.get(position);
            if (position > 2)
                return avUsersList.get(position - 3);
            else
                return null;
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
                //final String nickName = contacts.get(position - 3).name;
                final String nickName = avUsersList.get(position - 3).get("nickName").toString();

                // TODO 处理特殊字符
                String catalog = PingYinUtil.converterToFirstSpell(nickName);

                catalog = catalog.substring(0, 1);

                if (position == 3) {
                    viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                    viewHolder.tvCatalog.setText(catalog);
                } else {
                    String lastCatalog = PingYinUtil.converterToFirstSpell(
                            avUsersList.get(position - 4).get("nickName").toString()).substring(0, 1);
                    if (catalog.equals(lastCatalog)) {
                        viewHolder.tvCatalog.setVisibility(View.GONE);
                    } else {
                        viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                        viewHolder.tvCatalog.setText(catalog);
                    }
                }
                //设置头像
                Picasso.with(getActivity()).load(AVOSWrapper.getUserAvatarUrl(avUsersList.get(position - 3), 200, 200)).into(viewHolder.ivAvatar);

                //viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);//默认灰色头像
                viewHolder.tvNick.setText(nickName);//设置nickName
            }
            return convertView;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 0; i < avUsersList.size(); i++) {
                String l = PingYinUtil.converterToFirstSpell(avUsersList.get(i).getString("nickName"))
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

    public class Contact {
        public String name = null;
        public String phoneNumber = null;
        public boolean flag;//是否注册的标志

        /**
         * 判断是否已注册 *
         */
        public boolean hasSignedUp() {
            AVOSWrapper.queryUser("username", phoneNumber, new FindCallbackWrapper() {

                @Override
                public void onSucceed(List list) {
                    if (list.size() > 0) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                }

                @Override
                public void onFailed(AVException e) {
                    e.printStackTrace();
                }
            });
            return flag;
        }
    }

}
