package helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import app.DemoApplication;
import app.view.main.MainActivity;
import avos.AVOSWrapper;
import avos.callbackwrappers.LogInCallbackWrapper;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.HanziToPinyin;
import easemob.Constant;
import easemob.EaseMobHelper;
import easemob.db.UserDao;
import easemob.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhugongpu on 14-9-5.
 */

public class SweetyPlayHelper {

    private static final String TAG = "SweetyPlayHelper";

    /**
     * 初始化AVOS和环信
     */
    public static void init(Context appContext) {

        AVOSWrapper.init(appContext);
        EaseMobHelper.initEaseMob(appContext);
    }


    /**
     * 登录AVOS和环信,并进入主界面
     */
    public static void loginAsync(final Activity context, final String username, final String password, String message) {

        //提示progress bar

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setMessage(message);
        pd.show();
        AVOSWrapper.logInInBackground(username, password, new LogInCallbackWrapper() {

            @Override
            public void onSucceed(final AVUser user) {

                //环信登录
                // 调用sdk登陆方法登陆聊天服务器
                EMChatManager.getInstance().login(username, password, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // 登陆成功，保存用户名密码
                        DemoApplication.getInstance().setUsername(username);
                        DemoApplication.getInstance().setPassword(password);

                        //TODO 考虑采用avos
                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                pd.setMessage("正在获取好友和群聊列表...");
                            }
                        });
                        try {
                            // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
                            List<String> usernames = EMChatManager.getInstance().getContactUserNames();
                            Map<String, User> userlist = new HashMap<String, User>();
                            for (String username : usernames) {
                                User user = new User();
                                user.setUsername(username);
                                setUserHeader(username, user);
                                userlist.put(username, user);
                            }
                            // 添加user"申请与通知"
                            User newFriends = new User();
                            newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                            newFriends.setNick("申请与通知");
                            newFriends.setHeader("");
                            userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
                            // 添加"群聊"
                            User groupUser = new User();
                            groupUser.setUsername(Constant.GROUP_USERNAME);
                            groupUser.setNick("群聊");
                            groupUser.setHeader("");
                            userlist.put(Constant.GROUP_USERNAME, groupUser);

                            // 存入内存
                            DemoApplication.getInstance().setContactList(userlist);
                            // 存入db
                            UserDao dao = new UserDao(context);
                            List<User> users = new ArrayList<User>(userlist.values());
                            dao.saveContactList(users);

                            // 获取群聊列表(群聊里只有groupid和groupname的简单信息),sdk会把群组存入到内存和db中
                            EMGroupManager.getInstance().getGroupsFromServer();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        EMChatManager.getInstance()
                                .updateCurrentUserNick(DemoApplication.getInstance().getCurrentUserNickname());

                        if (!context.isFinishing())
                            pd.dismiss();

                        // 进入主页面
                        context.startActivity(new Intent(context, MainActivity.class));
                        context.finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, final String message) {

                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(context, "登录失败: " + message, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    /**
                     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
                     *
                     * @param username
                     * @param user
                     */
                    protected void setUserHeader(String username, User user) {
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
                            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
                            char header = user.getHeader().toLowerCase().charAt(0);
                            if (header < 'a' || header > 'z') {
                                user.setHeader("#");
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailed(AVException e) {
                // notify with error
                Toast.makeText(context, "登录失败: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 注册、登录并跳转到主界面
     *
     * @param context
     * @param user
     * @param username
     * @param password
     */
    public static void register(final Activity context, final AVUser user, final String username, final String password) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("正在注册...");
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Log.e(getClass().getName(), "TODO AVOS 注册");
                    //avos注册
                    user.signUp();
                    Log.e(TAG, "AVOS 注册完成");

                    //环信注册
                    EMChatManager.getInstance().createAccountOnServer(username, password);

                    Log.e(TAG, "环信 注册完成");

                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            if (!context.isFinishing())
                                pd.dismiss();
                            //保存用户信息
                            DemoApplication.getInstance().setUsername(username);
                            DemoApplication.getInstance().setPassword(password);
                            //登录
                            SweetyPlayHelper.loginAsync(context, username, password, "正在登录");
                        }
                    });

                } catch (final Exception e) {
                    e.printStackTrace();
                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            if (!context.isFinishing())
                                pd.dismiss();
                            if (e != null && e.getMessage() != null) {
                                String errorMsg = e.getMessage();
                                if (errorMsg.indexOf("EMNetworkUnconnectedException") != -1) {
                                    Toast.makeText(context.getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                                } else if (errorMsg.indexOf("conflict") != -1) {
                                    Toast.makeText(context.getApplicationContext(), "用户已存在！", Toast.LENGTH_SHORT).show();
                                }/* else if (errorMsg.indexOf("not support the capital letters") != -1) {
                                        Toast.makeText(getApplicationContext(), "用户名不支持大写字母！", 0).show();
									} */ else {
                                    Toast.makeText(context.getApplicationContext(), "注册失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(context.getApplicationContext(), "注册失败: 未知异常", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        }).start();
    }
}
