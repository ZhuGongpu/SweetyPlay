package easemob;

import android.content.Context;
import com.avos.avoscloud.AVUser;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;

/**
 * Created by zhugongpu on 14-9-4.
 */

public class EaseMobHelper {

    /**
     * 初始化
     * 在Application onCreate时使用
     *
     * @param appContext
     */
    public static void initEaseMob(Context appContext) {
        EMChat.getInstance().init(appContext);
        //获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        //默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //设置收到消息是否有新消息通知，默认为true
        options.setNotificationEnable(false);
        //设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(false);
        //设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(false);
        //设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(false);
    }

    /**
     * 注册用户
     * 当avuser注册成功时调用,将用户同时注册到环信
     *
     * @param username 不能包含大写字母
     * @param password
     */
    private static void register(final String username, final String password) {
        final String appkey = EMChatConfig.getInstance().APPKEY;
        new Thread(new Runnable() {
            public void run() {
                try {
                    //调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(appkey + "_" + username, password);
                } catch (final Exception e) {
                    //TODO
                }
            }
        }).start();
    }

    /**
     * 注册用户
     * 当avuser注册成功时调用,将用户同时注册到环信
     *
     * @param user AVUser用户
     */
    public static void register(AVUser user) {
        register(user.getUsername(), user.getString("password"));
    }

    /**
     * 调用sdk登陆方法登陆聊天服务器
     * 当avuser登录成功时使用，同时登录到环信服务器
     *
     * @param username
     * @param password
     * @param callBack
     */
    private static void login(String username, String password, EMCallBack callBack) {
        EMChatManager.getInstance().login(username, password, callBack);
    }

    /**
     * 调用sdk登陆方法登陆聊天服务器
     * 当avuser登录成功时使用，同时登录到环信服务器
     *
     * @param user     AVUser用户
     * @param callBack
     */
    public static void login(AVUser user, EMCallBack callBack) {

        login(user.getUsername(), user.getString("password"), callBack);
    }

    /**
     * 登出聊天服务器
     * 当avuser登出时使用
     */
    public static void logout() {
        EMChatManager.getInstance().logout();
    }

    /**
     * 此方法主要为了在苹果推送时能够推送昵称(nickname)而不是userid,
     * 一般可以在登陆成功后从自己服务器获取到个人信息，然后拿到nick更新到环信服务器。
     * 并且，在个人信息中如果更改个人的昵称，也要把环信服务器更新下nickname 防止显示差异。
     *
     * @param nickname
     * @return 此方法传入一个字符串String类型的参数，返回成功或失败的一个Boolean类型的返回值
     */
    public static boolean updateCurrentUserNickname(String nickname) {
        return EMChatManager.getInstance().updateCurrentUserNick(nickname);
    }

}
