package utils;

/**
 * 用于判断密码格式是否合法
 * Created by zhugongpu on 14-7-7.
 */

public class PasswordFormatValidator {

    public static boolean isLegalPassword(String password) {
        if (password == null)
            return false;

        //the length of password should between 6~20
        int length = password.length();
        if (length < 6 || length > 20)
            return false;

//        //the password should not be total number or alphas
//        int countNumber = 0, countAlpas = 0;
//
//        byte[] bytes = password.getBytes();
//
//        for (int i = 0; i < password.length(); i++) {
//            if (bytes[i] >= 48 && bytes[i] <= 57)
//                countNumber++;
//
//            if ((bytes[i] >= 65 && bytes[i] <= 90) || (bytes[i] >= 97 && bytes[i] <= 122))
//                countAlpas++;
//        }
//
//        if ((countNumber == password.length()) || (countAlpas == password.length()))
//            return false;


        return true;
    }
}
