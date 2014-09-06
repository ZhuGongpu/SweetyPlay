package app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import app.view.chat.ChatActivity;
import app.view.main.MainActivity;
import avos.AVOSWrapper;
import com.easemob.chat.*;
import easemob.db.DbOpenHelper;
import easemob.db.UserDao;
import easemob.domain.User;
import utils.PreferenceUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhugongpu on 14-9-5.
 */
public class DemoApplication extends Application {

    // login user name
    public static final String SHARED_PREFERENCES_USERNAME = "USERNAME";
    // login password
    private static final String SHARED_PREFERENCES_PWD = "PASSWORD";
    public static Context applicationContext;

    private static DemoApplication instance;
    private String username = null;
    private String password = null;
    private Map<String, User> contactList;

    public static DemoApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
        if (processAppName == null || processAppName.equals("")) {
            // workaround for baidu location sdk
            // 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
            // 创建新的进程。
            // 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
            // processName，
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        applicationContext = this;
        instance = this;

        //初始化avos
        AVOSWrapper.init(this);

//        EMChat.getInstance().setDebugMode(true);
        // 初始化环信SDK,一定要先调用init()
        EMChat.getInstance().init(applicationContext);
        Log.d("EMChat Demo", "initialize EMChat SDK");
        // debugmode设为true后，就能看到sdk打印的log了

        // 获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置收到消息是否有新消息通知，默认为true
        options.setNotificationEnable(PreferenceUtils.getInstance(applicationContext).getSettingMsgNotification());
        // 设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(PreferenceUtils.getInstance(applicationContext).getSettingMsgSound());
        // 设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(PreferenceUtils.getInstance(applicationContext).getSettingMsgVibrate());
        // 设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(PreferenceUtils.getInstance(applicationContext).getSettingMsgSpeaker());
        // 设置notification消息点击时，跳转的intent为自定义的intent
        options.setOnNotificationClickListener(new OnNotificationClickListener() {

            @Override
            public Intent onNotificationClick(EMMessage message) {
                Intent intent = new Intent(applicationContext, ChatActivity.class);
                EMMessage.ChatType chatType = message.getChatType();
                if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                } else { // 群聊信息
                    // message.getTo()为群聊id
                    intent.putExtra("groupId", message.getTo());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                }
                return intent;
            }
        });
        // 设置一个connectionlistener监听账户重复登陆
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
//		// 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
//		options.setNotifyText(new OnMessageNotifyListener() {
//
//			@Override
//			public String onNewMessageNotify(EMMessage message) {
//				// 可以根据message的类型提示不同文字(可参考微信或qq)，demo简单的覆盖了原来的提示
//				return "你的好基友" + message.getFrom() + "发来了一条消息哦";
//			}
//
//			@Override
//			public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
//				return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
//			}
//
//			@Override
//			public String onSetNotificationTitle(EMMessage message) {
//				//修改标题
//				return "环信notification";
//			}
//
//
//		});

    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        if (getUsername() != null && contactList == null) {
            UserDao dao = new UserDao(applicationContext);
            // 获取本地好友user list到内存,方便以后获取好友list
            contactList = dao.getContactList();
        }
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        this.contactList = contactList;
    }

    public void setStrangerList(Map<String, User> List) {

    }

    public String getCurrentUserNickname() {
        return AVOSWrapper.getCurrentUser().getString("nickName");
    }

    /**
     * 获取当前登陆用户id
     *
     * @return
     */
    public String getUsername() {
        if (username == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            username = preferences.getString(SHARED_PREFERENCES_USERNAME, null);
        }
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username
     */
    public void setUsername(String username) {
        if (username != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            SharedPreferences.Editor editor = preferences.edit();
            if (editor.putString(SHARED_PREFERENCES_USERNAME, username).commit()) {
                this.username = username;
            }
        }
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        if (password == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            password = preferences.getString(SHARED_PREFERENCES_PWD, null);
        }
        return password;
    }

    /**
     * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
     * 内部的自动登录需要的密码，已经加密存储了
     *
     * @param pwd
     */
    public void setPassword(String pwd) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(SHARED_PREFERENCES_PWD, pwd).commit()) {
            password = pwd;
        }
    }

    /**
     * 退出登录,清空数据
     */
    public void logout() {
        // 先调用sdk logout，在清理app中自己的数据
        EMChatManager.getInstance().logout();
        DbOpenHelper.getInstance(applicationContext).closeDB();
        // reset password to null
        setPassword(null);
        setContactList(null);

    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    class MyConnectionListener implements ConnectionListener {
        @Override
        public void onReConnecting() {
        }

        @Override
        public void onReConnected() {
        }

        @Override
        public void onDisConnected(String errorString) {
            if (errorString != null && errorString.contains("conflict")) {
                Intent intent = new Intent(applicationContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("conflict", true);
                startActivity(intent);
            }

        }

        @Override
        public void onConnecting(String progress) {

        }

        @Override
        public void onConnected() {
        }
    }
}
