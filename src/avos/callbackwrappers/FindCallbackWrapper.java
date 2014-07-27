package avos.callbackwrappers;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;

import java.util.List;

/**
 * Created by zhugongpu on 14-7-27.
 */

public class FindCallbackWrapper extends FindCallback {
    @Override
    public void done(List list, AVException e) {
        if (e == null)
            onSucceed(list);
        else
            onFailed(e);
    }


    public void onSucceed(List list) {
    }

    public void onFailed(AVException e) {

    }
}
