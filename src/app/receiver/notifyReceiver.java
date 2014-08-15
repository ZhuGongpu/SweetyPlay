package app.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import app.view.login.R;
import app.view.main.notify.NotificationActivity;
import com.avos.avoscloud.LogUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lewis on 8/14/14.
 */
public class notifyReceiver extends BroadcastReceiver {

    private static final String TAG = "notifyuReceiver";
    private String message, userId;
    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        LogUtil.log.e(TAG, "GET BroadCast");


        String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");

        //根据channel的不同来进行处理得到的数据
        if (channel != null) {

            //处理bubble
            if (channel.equals("bubble")) {
                try {
                    JSONObject json = new JSONObject(
                            intent.getExtras().getString("com.avos.avoscloud.Data"));

                    if (json != null) {
                        message = json.getString("message");
                        userId = json.getString("userId");
                        //创建一个notification
                        popNotification(context);

                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());


                }


            } else if (channel.equals("invite")) {
                //todo 处理得到邀请的信息
            }

        }


    }

    //rasie a notifycation
    private void popNotification(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        //create notification
        int icon = R.drawable.logo;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, message, when);

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        notification.defaults = notification.DEFAULT_SOUND | notification.DEFAULT_VIBRATE;


        //start Activity
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        intent.putExtra("userId", userId);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        notification.setLatestEventInfo(context, "Bubble", "Message", pendingIntent);

        //send notification
        mNotificationManager.notify(0, notification);
    }


}
