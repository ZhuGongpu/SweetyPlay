package app.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 获取本机号码相关的处理函数
 * Created by zhugongpu on 14-7-12.
 */

public class PhoneNumberUtility {
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return extractPhoneNumber(telephonyManager.getLine1Number());
    }

    /**
     * 从一段字符串中提取出手机号码
     *
     * @param rawNumbers 未经处理且可能包含手机号码的字符串
     * @return 手机号码
     */
    public static String extractPhoneNumber(String rawNumbers) {
        if (rawNumbers != null) {
            //TODO

            if (rawNumbers.startsWith("+86"))
                return rawNumbers.substring(3);

            return rawNumbers;
        } else
            return null;
    }
}
