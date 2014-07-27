package avos.callbackwrappers;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.RequestPasswordResetCallback;

/**
 * Created by zhugongpu on 14-7-27.
 */

public class RequestPasswordResetCallbackWrapper extends RequestPasswordResetCallback {
    @Override
    public void done(AVException e) {
        if (e == null)
            onSucceed();
        else
            onFailed(e);
    }

    public void onSucceed() {
    }

    public void onFailed(AVException e) {

    }
}
