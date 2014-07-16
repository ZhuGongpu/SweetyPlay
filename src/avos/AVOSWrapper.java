package avos;

import android.content.Context;
import com.avos.avoscloud.*;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhugongpu on 14-7-12.
 */

public class AVOSWrapper {

    private static final String TAG = "AVOSWrapper";


    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        AVOSCloud.initialize(context, "fas3aqr6wocde9up6hp1acytgijs3rm09uq34w0gkylhicwj", "iv7mh6kzhy99zxeoxurmy13ny2t6k7eh0fou27w96l88567o");
    }

    /**
     * 异步注册用户
     *
     * @param user
     * @param callback
     */
    public static void signUpInBackground(AVUser user, SignUpCallback callback) {
        user.signUpInBackground(callback);
    }

    /**
     * 异步登录
     *
     * @param userName
     * @param password
     * @param callback
     */
    public static void logInInBackground(String userName, String password, LogInCallback callback) {
        AVUser.logInInBackground(userName, password, callback);
    }


    /**
     * 如果用户在每次打开你的应用程序时都要登录，这将会直接影响到你应用的用户体验。为了避免这种情况，你可以使用缓存的 currentUser 对象。
     * <p/>
     * 每当你注册成功或是第一次登录成功，都会在本地磁盘中有一个缓存的用户对象，你可以这样来获取这个缓存的用户对象来进行登录
     *
     * @return
     */
    public static AVUser getCurrentUser() {
        return AVUser.getCurrentUser();
    }

    /**
     * 根据邮箱地址重置密码
     *
     * @param emailAddress
     * @param callback
     */

    public static void resetPassword(String emailAddress, RequestPasswordResetCallback callback) {
        AVUser.requestPasswordResetInBackground(emailAddress, callback);
    }


    /**
     * 查找符合条件的用户
     *
     * @param attributeName  需要限定的属性名
     * @param attributeValue 对应的值
     * @param callback
     */
    public static void queryUser(String attributeName, Object attributeValue, FindCallback callback) {
        AVQuery<AVUser> query = AVQuery.getUserQuery();
        query.whereEqualTo(attributeName, attributeValue);
        query.findInBackground(callback);
    }


    /**
     * 登出，并清除缓存用户对象
     */
    public static void logout() {
        AVUser.logOut();
    }


    /**
     * 上传文件
     *
     * @param file             需要上传的文件 文件大小不能超过10M
     * @param fileName         文件名
     * @param saveCallback     回调函数
     * @param progressCallback 进度回调函数
     * @throws IOException
     */
    public static void uploadFile(File file, String fileName, SaveCallback saveCallback, ProgressCallback progressCallback) throws IOException {
        AVFile.withFile(fileName, file).saveInBackground(saveCallback, progressCallback);
    }


    public static void downloadFile(AVFile file, GetDataCallback dataCallback, ProgressCallback progressCallback) {
        file.getDataInBackground(dataCallback, progressCallback);
    }

    /**
     * 获取图片的缩略图
     *
     * @param file
     * @param width
     * @param height
     * @return
     */
    public static String getThubnailUrl(AVFile file, int width, int height) {
        return file.getThumbnailUrl(true, width, height);
    }
}
