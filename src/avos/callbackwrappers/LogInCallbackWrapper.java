package avos.callbackwrappers;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

/**
 * Created by zhugongpu on 14-7-27.
 */

public abstract class LogInCallbackWrapper extends LogInCallback {
    @Override
    public void done(AVUser avUser, AVException e) {
        if (avUser != null)
            onSucceed(avUser);
        else
            onFailed(e);
    }

    public abstract void onSucceed(AVUser user);

    public abstract void onFailed(AVException e);
}
