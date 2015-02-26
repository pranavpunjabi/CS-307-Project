package cs307.com.pranav.getguru;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by DiGiT_WiZARD on 2/26/15.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int arg0) {
        Bundle data = new Bundle();
        switch(arg0){

            /** Android tab is selected */
            case 0:
                MyFriendsListFragment myfriends = new MyFriendsListFragment();
                data.putInt("current_page", arg0+1);
                myfriends.setArguments(data);
                return myfriends;

            case 1:
                MyAndroidVersionListFragment androidversion = new MyAndroidVersionListFragment();
                data.putInt("current_page", arg0+1);
                androidversion.setArguments(data);
                return androidversion;

            case 2:
                MyAndroidPhonesListFragment androidphone = new MyAndroidPhonesListFragment();
                data.putInt("current_page", arg0+1);
                androidphone.setArguments(data);
                return androidphone;
        }

        return null;
    }

    /** Returns the number of pages */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
