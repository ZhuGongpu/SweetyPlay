package utils;

import com.avos.avoscloud.AVUser;

/**
 * Created by Liu TianYi on 2014/8/14.
 */

public class AVUserComparator extends PinyinComparator {
    @Override
    public int compare(Object o1, Object o2) {

        String name1 = ((AVUser) o1).get("nickName").toString();
        String name2 = ((AVUser) o2).get("nickName").toString();
        return super.compare(name1, name2);
    }
}
