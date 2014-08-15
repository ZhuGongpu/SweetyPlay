package avos;

import android.content.Context;
import avos.callbackwrappers.*;
import avos.models.PlayEntity;
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
        AVObject.registerSubclass(PlayEntity.class);
        AVOSCloud.initialize(context, "dwhw1lxrrq87fiprvz1bls56yboxnnawe0rnuipzqsoh8vq7", "tquta7xm8xrqaxrj0jp5h38h4sfe6kxyr48v86uy979yz65m");
    }

    /**
     * 异步注册用户
     *
     * @param user
     * @param callback
     */
    public static void signUpInBackground(AVUser user, SignUpCallbackWrapper callback) {
        user.signUpInBackground(callback);
    }

    /**
     * 异步登录
     *
     * @param userName
     * @param password
     * @param callback
     */
    public static void logInInBackground(String userName, String password, LogInCallbackWrapper callback) {
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

    public static void resetPassword(String emailAddress, RequestPasswordResetCallbackWrapper callback) {
        AVUser.requestPasswordResetInBackground(emailAddress, callback);
    }


    /**
     * 查找符合条件的用户
     *
     * @param attributeName  需要限定的属性名
     * @param attributeValue 对应的值
     * @param callback
     */
    public static void queryUser(String attributeName, Object attributeValue, FindCallbackWrapper callback) {
        AVQuery<AVUser> query = AVQuery.getUserQuery();
        query.whereEqualTo(attributeName, attributeValue);
        query.findInBackground(callback);
    }

    /**
     * 根据号码查询用户
     *
     * @param phoneNumber 用户号码
     * @param callback
     */
    public static void queryUser(String phoneNumber, FindCallbackWrapper<AVUser> callback) {
        queryUser("phone_number", phoneNumber, callback);
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
     * @throws java.io.IOException
     */
    public static void uploadFile(File file, String fileName, SaveCallbackWraprer saveCallback, ProgressCallback progressCallback) throws IOException {
        AVFile.withFile(fileName, file).saveInBackground(saveCallback, progressCallback);
    }


    public static void downloadFile(AVFile file, GetDataCallback dataCallback, ProgressCallback progressCallback) {
        file.getDataInBackground(dataCallback, progressCallback);
    }

    /**
     * 获取图片的缩略图
     *
     * @param file   图片文件
     * @param width  图片目标宽度
     * @param height 图片目标高度
     * @return 图片url
     */
    public static String getThumbnailUrl(AVFile file, int width, int height) {
        if (file != null)
            return file.getThumbnailUrl(true, width, height);
        else
            return null;
    }


    /**
     * 获取用户头像
     *
     * @param user        用户
     * @param imageWidth  图片目标宽度
     * @param imageHeight 图片目标高度
     * @return
     */
    public static String getUserAvatarUrl(AVUser user, int imageWidth, int imageHeight) {
        return getThumbnailUrl(user.getAVFile("Photo"), imageWidth, imageHeight);
    }


    /**
     * 获取用户信息
     *
     * @param user          用户
     * @param attributeName 属性名
     * @return 需要根据具体属性进行类型转换
     */
    public static Object getUserInfo(AVUser user, String attributeName) {
        if (user != null)
            return user.get(attributeName);
        else
            return null;

    }

    /**
     * 查询附近的Play
     *
     * @param currentGeoPoint 当前位置
     * @param sizeLimit       返回结果数量限制
     * @param callback        用于接收结果
     */
    public static void queryNearbyPlays(AVGeoPoint currentGeoPoint, int sizeLimit, FindCallbackWrapper<PlayEntity> callback) {
        AVQuery<PlayEntity> query = new AVQuery<PlayEntity>("Play");
        query.whereNear("Place", currentGeoPoint);
        query.setLimit(sizeLimit);
        query.findInBackground(callback);
    }

    /**
     * 向服务器的Play列表请求数据
     *
     * @param skip      忽略的数量
     * @param sizeLimit 结果数量限制
     * @param callback  接收结果
     */
    public static void queryPlays(int skip, int sizeLimit, FindCallbackWrapper<PlayEntity> callback) {
        AVQuery<PlayEntity> query = new AVQuery<PlayEntity>("Play");
        query.setLimit(sizeLimit);
        query.setSkip(skip);
        query.findInBackground(callback);
    }

    /**
     * 向服务器的Play列表请求数据
     *
     * @param sizeLimit 结果数量限制
     * @param callback  接收结果
     */
    public static void queryPlays(int sizeLimit, FindCallbackWrapper<PlayEntity> callback) {
        queryPlays(0, sizeLimit, callback);
    }


    /**
     * 根据 id 查找play
     *
     * @param playID
     * @param callback
     */
    public static void queryPlay(String playID, FindCallbackWrapper<PlayEntity> callback) {
        AVQuery<PlayEntity> query = new AVQuery<PlayEntity>("Play");
        query.whereEqualTo("objectId", playID);
        query.findInBackground(callback);
    }

    /**
     * 获取用户好友列表
     *
     * @param user
     * @return Relation
     */
    public static AVRelation<AVUser> getUserRelation(AVUser user) {
        return user.getRelation("FriendList");
    }

    /**
     * 获取好友列表
     *
     * @param user
     * @param callback 处理返回的好友列表
     */
    public static void getFriendList(AVUser user, FindCallbackWrapper<AVUser> callback) {
        getUserRelation(user).getQuery().findInBackground(callback);
    }

    /**
     * query for general purpose
     *
     * @param query
     * @param callback
     */
    public static void query(AVQuery query, FindCallbackWrapper callback) {
        query.findInBackground(callback);
    }
}
