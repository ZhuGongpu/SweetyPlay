package app.view.friendspage;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import app.util.ContactComparator;
import app.util.PingYinUtil;
import app.util.SideBar;
import app.view.login.R;

import java.util.ArrayList;
import java.util.Collections;

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
    protected View view;
    ArrayList<Contact> contacts = new ArrayList<Contact>();
    private ListView lvContact;
    private ContactAdapter contactAdapter = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.arg1 == UPDATE_SIGNAL) {

                contactAdapter = new ContactAdapter(getActivity());

                lvContact.setAdapter(contactAdapter);

                contactAdapter.notifyDataSetChanged();
            }
        }
    };
    private SideBar indexBar;
    private WindowManager mWindowManager;
    private TextView mDialogText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friendlist, container, false);

        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        findView();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (contacts.isEmpty())
                    getPhoneContacts();
                return null;
            }

        }.execute();

        return view;
    }

    private void findView() {
        lvContact = (ListView) view.findViewById(R.id.lvContact);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0 || i == 1 || i == 2) {
                    Toast.makeText(getActivity().getApplicationContext(), "i am " + i, Toast.LENGTH_SHORT).show();
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
                contactName.replace(" ", "");

                //Sim卡中没有联系人头像
                Contact contact = new Contact();
                contact.name = contactName;
                contact.phoneNumber = phoneNumber;

                contacts.add(contact);
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
                contactName.replace(" ", "");
                //TODO 获取头像

                Contact contact = new Contact();
                contact.name = contactName;
                contact.phoneNumber = phoneNumber;

                contacts.add(contact);
            }

            //排序
            Collections.sort(contacts, new ContactComparator());

            phoneCursor.close();

            Message message = handler.obtainMessage();
            message.arg1 = UPDATE_SIGNAL;
            message.sendToTarget();
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
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
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
                final String nickName = contacts.get(position - 3).name;

                //Log.e(TAG, "" + position + ": " + nickName);
                // TODO 处理特殊字符
                String catalog = PingYinUtil.converterToFirstSpell(nickName);
                //Log.e(TAG, "" + position + ": " + nickName + "    " + (catalog));

                catalog = catalog.substring(0, 1);

                if (position == 3) {
                    viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                    viewHolder.tvCatalog.setText(catalog);
                } else {
                    String lastCatalog = PingYinUtil.converterToFirstSpell(
                            contacts.get(position - 4).name).substring(0, 1);
                    if (catalog.equals(lastCatalog)) {
                        viewHolder.tvCatalog.setVisibility(View.GONE);
                    } else {
                        viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                        viewHolder.tvCatalog.setText(catalog);
                    }
                }
                viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);
                viewHolder.tvNick.setText(nickName);
            }
            return convertView;
        }

        @Override
        public int getPositionForSection(int section) {
            for (int i = 0; i < contacts.size(); i++) {
                String l = PingYinUtil.converterToFirstSpell(contacts.get(i).name)
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

    }

}
