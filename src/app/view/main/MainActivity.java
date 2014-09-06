/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.view.main;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.DemoApplication;
import app.view.chat.ChatActivity;
import app.view.chat.ChatAllHistoryFragment;
import app.view.chat.ContactListFragment;
import app.view.chat.GroupsActivity;
import app.view.circle.CircleFragment;
import app.view.login.LoginActivity;
import app.view.login.R;
import app.view.play_list.PlayListFragment;
import com.easemob.chat.*;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import easemob.Constant;
import easemob.db.InviteMessageDao;
import easemob.db.UserDao;
import easemob.domain.InviteMessage;
import easemob.domain.User;
import utils.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends FragmentActivity {

    protected static final String TAG = "MainActivity";
    // 未读消息textview
    private TextView unreadMessageLabel;
    // 未读通讯录textview
    private TextView unreadContactLabel;

    private Button[] mTabs;

    /**
     * Fragments
     */
    private PlayListFragment playListFragment = null;
    private CircleFragment circleFragment = null;
    private ChatAllHistoryFragment chatHistoryFragment;
    private ContactListFragment contactListFragment;
    private Fragment[] fragments;

    private int index;
    private RelativeLayout[] tab_containers;
    // 当前fragment的index
    private int currentTabIndex;
    private NewMessageBroadcastReceiver msgReceiver;
    // 账号在别处登录
    private boolean isConflict = false;
    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");
            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);
                if (msg != null) {
                    msg.isAcked = true;
                }
            }
            abortBroadcast();
        }
    };

    /**
     * 离线消息BroadcastReceiver
     * sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI 有哪些人发来了离线消息
     * UI 可以做相应的操作，比如下载用户信息
     */
    private BroadcastReceiver offlineMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String[] users = intent.getStringArrayExtra("fromuser");
            String[] groups = intent.getStringArrayExtra("fromgroup");
            if (users != null) {
                for (String user : users) {
                    System.out.println("收到user离线消息：" + user);
                }
            }
            if (groups != null) {
                for (String group : groups) {
                    System.out.println("收到group离线消息：" + group);
                }
            }
            abortBroadcast();
        }
    };
    private InviteMessageDao inviteMessageDao;
    private UserDao userDao;
    private android.app.AlertDialog.Builder conflictBuilder;
    private boolean isConflictDialogShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        initView();
        inviteMessageDao = new InviteMessageDao(this);
        userDao = new UserDao(this);

        //设置fragments
        playListFragment = new PlayListFragment();
        circleFragment = new CircleFragment();
        chatHistoryFragment = new ChatAllHistoryFragment();//会话
        contactListFragment = new ContactListFragment();//通讯录
        fragments = new Fragment[]{playListFragment, circleFragment, chatHistoryFragment, contactListFragment};


        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatHistoryFragment)
                .add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(chatHistoryFragment)
                .commit();

        // 注册一个接收消息的BroadcastReceiver
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
                .getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        // 注册一个离线消息的BroadcastReceiver
        IntentFilter offlineMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
                .getOfflineMessageBroadcastAction());
        registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);

        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());
        // 注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
        // 注册群聊相关的listener
        EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        unreadMessageLabel = (TextView) findViewById(R.id.unread_message_number);
        unreadContactLabel = (TextView) findViewById(R.id.unread_friend_number);
        mTabs = new Button[4];
        mTabs[0] = (Button) findViewById(R.id.tabbar_playlist);
        mTabs[1] = (Button) findViewById(R.id.tabbar_circles);
        mTabs[2] = (Button) findViewById(R.id.tabbar_message);
        mTabs[3] = (Button) findViewById(R.id.tabbar_friends);
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);
    }

    /**
     * button点击事件
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.tabbar_playlist:
                index = 0;
                break;
            case R.id.tabbar_circles:
                index = 1;
                break;
            case R.id.tabbar_message:
                index = 2;
                break;
            case R.id.tabbar_friends:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                transaction.add(R.id.fragment_container, fragments[index]);
            }
            transaction.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收者
        try {
            unregisterReceiver(msgReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(ackMessageReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(offlineMessageReceiver);
        } catch (Exception e) {
        }

        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }

    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            unreadMessageLabel.setText(String.valueOf(count));
            unreadMessageLabel.setVisibility(View.VISIBLE);
        } else {
            unreadMessageLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadAddressCountTotal();
                if (count > 0) {
                    unreadContactLabel.setText(String.valueOf(count));
                    unreadContactLabel.setVisibility(View.VISIBLE);
                } else {
                    unreadContactLabel.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        if (DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME) != null)
            unreadAddressCountTotal = DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME)
                    .getUnreadMsgCount();
        return unreadAddressCountTotal;
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        return unreadMsgCountTotal;
    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
        // 刷新好友页面ui
        if (currentTabIndex == 1)
            contactListFragment.refresh();
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessageDao.saveMessage(msg);
        // 未读数加1
        User user = DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME);
        user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
    }


    /**
     * set head
     *
     * @param username
     * @return
     */
    User setUserHead(String username) {
        User user = new User();
        user.setUsername(username);
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
                    0, 1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConflict) {
            updateUnreadLabel();
            updateUnreadAddressLable();
            EMChatManager.getInstance().activityResumed();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        DemoApplication.getInstance().logout();

        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                conflictBuilder.setTitle("下线通知");
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                Log.e("###", "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow)
            showConflictDialog();
    }

    /**
     * 新消息广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

            // 消息id
            String msgId = intent.getStringExtra("msgid");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            // EMMessage message =
            // EMChatManager.getInstance().getMessage(msgId);

            // 刷新bottom bar消息未读数
            updateUnreadLabel();
            if (currentTabIndex == 0) {
                // 当前页面如果为聊天历史页面，刷新此页面
                if (chatHistoryFragment != null) {
                    chatHistoryFragment.refresh();
                }
            }
            // 注销广播，否则在ChatActivity中会收到这个广播
            abortBroadcast();
        }
    }

    /**
     * 好友变化listener
     */
    private class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
            Map<String, User> toAddUsers = new HashMap<String, User>();
            for (String username : usernameList) {
                User user = setUserHead(username);
                // 暂时有个bug，添加好友时可能会回调added方法两次
                if (!localUsers.containsKey(username)) {
                    userDao.saveContact(user);
                }
                toAddUsers.put(username, user);
            }
            localUsers.putAll(toAddUsers);
            // 刷新ui
            if (currentTabIndex == 1)
                contactListFragment.refresh();

        }


        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessageDao.deleteMessage(username);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    //如果正在与此用户的聊天页面
                    if (ChatActivity.activityInstance != null && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + "已把你从他好友列表里移除", Toast.LENGTH_LONG).show();
                        ChatActivity.activityInstance.finish();
                    }
                    updateUnreadLabel();
                }
            });
            // 刷新ui
            if (currentTabIndex == 1)
                contactListFragment.refresh();

        }

        @Override
        public void onContactInvited(String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
            List<InviteMessage> msgs = inviteMessageDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessageDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现

        }

    }

    /**
     * 连接监听listener
     */
    private class MyConnectionListener implements ConnectionListener {

        @Override
        public void onConnected() {
            chatHistoryFragment.errorItem.setVisibility(View.GONE);
        }

        @Override
        public void onDisConnected(String errorString) {
            if (errorString != null && errorString.contains("conflict")) {
                // 显示帐号在其他设备登陆dialog
                showConflictDialog();
            } else {
                chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
                if (NetUtils.hasNetwork(MainActivity.this))
                    chatHistoryFragment.errorText.setText("连接不到聊天服务器");
                else
                    chatHistoryFragment.errorText.setText("当前网络不可用，请检查网络设置");

            }
        }

        @Override
        public void onReConnected() {
            chatHistoryFragment.errorItem.setVisibility(View.GONE);
        }

        @Override
        public void onReConnecting() {
        }

        @Override
        public void onConnecting(String progress) {
        }

    }

    /**
     * MyGroupChangeListener
     */
    private class MyGroupChangeListener implements GroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            boolean hasGroup = false;
            for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

            // 被邀请
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(inviter + "邀请你加入了群聊"));
            // 保存邀请消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 0)
                        chatHistoryFragment.refresh();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            });

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            // 提示用户被T了，demo省略此步骤
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        updateUnreadLabel();
                        if (currentTabIndex == 0)
                            chatHistoryFragment.refresh();
                        if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                            GroupsActivity.instance.onResume();
                        }
                    } catch (Exception e) {
                        Log.e("###", "refresh exception " + e.getMessage());
                    }

                }
            });
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            // 群被解散
            // 提示用户群被解散,demo省略
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    if (currentTabIndex == 0)
                        chatHistoryFragment.refresh();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            });

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
            //加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
            msg.setChatType(ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + "同意了你的群聊申请"));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 0)
                        chatHistoryFragment.refresh();
                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }
            });
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            //加群申请被拒绝，demo未实现
        }

    }
}
