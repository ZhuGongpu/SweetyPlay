package quickblox;

import com.quickblox.core.QBCallbackImpl;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBProvider;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;

import java.io.File;

/**
 * Created by zhugongpu on 14-7-6.
 */

public class QuickBloxSupportingLibrary {

    private static String TAG = "QuickBloxSupportingLibrary";

    /**
     * In order to use QuickBlox Users APIs (any QuickBlox API) you must create session.
     * Then you will have READ access in QuickBlox environment
     *
     * @param callback 用于处理返回的result
     */
    public static void createSession(QBCallbackImpl callback) {
        QBAuth.createSession(callback);
    }


    /**
     * Create new User (registration)
     *
     * @param user     Only login/email and password fields are required
     * @param callback 回调函数
     */
    public static void signUp(QBUser user, QBCallbackImpl callback) {
        QBUsers.signUp(user, callback);
    }


    /**
     * sign in with user
     *
     * @param user
     * @param callback
     */
    public static void signIn(QBUser user, QBCallbackImpl callback) {
        QBUsers.signIn(user, callback);
    }

    /**
     * sign in with login and password
     *
     * @param login    用户名
     * @param password 密码
     * @param callback
     */
    public static void signIn(String login, String password, QBCallbackImpl callback) {
        signIn(new QBUser(login, password), callback);
    }


    /**
     * Sign In using Facebook access token
     *
     * @param facebookAccessToken
     * @param callback
     */
    public static void signInUsingFacebook(String facebookAccessToken, QBCallbackImpl callback) {
        QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, facebookAccessToken, null, callback);
    }

    /**
     * Sign In using Facebook access token
     *
     * @param twitterAccessToken
     * @param callback
     */
    public static void signInUsingTwitter(String twitterAccessToken, QBCallbackImpl callback) {
        QBUsers.signInUsingSocialProvider(QBProvider.FACEBOOK, twitterAccessToken, null, callback);
    }


    /**
     * update user profile
     *
     * @param user
     * @param callback
     */
    public static void updateUserProfile(QBUser user, QBCallbackImpl callback) {
        //TODO
    }

    /**
     * update profile picture
     *
     * @param picture
     * @param callback
     */
    public static void updateProfilePicture(File picture, QBCallbackImpl callback) {
        //TODO

    }

    /**
     * Email with instruction will be send to the email address
     *
     * @param emailAddress
     * @param callback
     */
    public static void resetPassword(String emailAddress, QBCallbackImpl callback) {
        QBUsers.resetPassword(emailAddress, callback);
    }

    /**
     * sign out
     * @param callback
     */
    public static void signOut(QBCallbackImpl callback) {
        QBUsers.signOut(callback);
    }

    //TODO retrieve user
}
