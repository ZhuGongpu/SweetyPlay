package app.util;

import app.view.friendspage.FriendsFragment;

/**
 * Created by zhugongpu on 14-8-10.
 */

public class ContactComparator extends PinyinComparator {
    @Override
    public int compare(Object o1, Object o2) {

        String name1 = ((FriendsFragment.Contact) o1).name;
        String name2 = ((FriendsFragment.Contact) o2).name;
        return super.compare(name1, name2);
    }
}
