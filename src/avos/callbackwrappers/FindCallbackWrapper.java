package avos.callbackwrappers;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;

import java.util.List;

/**
 * Created by zhugongpu on 14-7-27.
 */

public abstract class FindCallbackWrapper<T extends AVObject> extends FindCallback {
    @Override
    public void done(List list, AVException e) {
        if (e == null)
            onSucceed(list);
        else
            onFailed(e);
    }

    public abstract void onSucceed(List<T> list);

    public abstract void onFailed(AVException e);
}
