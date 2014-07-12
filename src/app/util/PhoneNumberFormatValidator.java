package app.util;

/**
 * 用于检查电话号码格式是否合法
 * Created by zhugongpu on 14-7-7.
 */

public class PhoneNumberFormatValidator {


    public static boolean isLegalPhoneNumber(String phoneNumber) {
        if (phoneNumber == null)
            return false;

        //TODO
        phoneNumber = phoneNumber.trim();
        if (phoneNumber.startsWith("+")) {//以+号起始
            phoneNumber = phoneNumber.substring(3, phoneNumber.length() - 1).trim();
            if (phoneNumber.length() == 11) {
                char[] phoneNumberArray = phoneNumber.toCharArray();
                for (int i = 0; i < phoneNumberArray.length; i++) {
                    if (!(phoneNumberArray[i] >= '0' && phoneNumberArray[i] <= '9')) {
                        return false;
                    }
                }
                return true;
            } else
                return false;
        } else {
            if (phoneNumber.length() == 11) {
                char[] phoneNumberArray = phoneNumber.toCharArray();
                for (int i = 0; i < phoneNumberArray.length; i++) {
                    if (!(phoneNumberArray[i] >= '0' && phoneNumberArray[i] <= '9')) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }
}
