package com.aligame.jcourse.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aligame.jcourse.fragment.AudioListFragment;
import com.aligame.jcourse.fragment.VideoListFragment;

/**
 * Created by lvrh on 17/3/3.
 */

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int arg0) {
        Fragment ft;
        switch (arg0) {
            case 0:
                ft = new AudioListFragment();
                break;
            case 1:
            case 2:
                ft = new VideoListFragment();
                break;
            default:
                ft = new AudioListFragment();
                break;
        }
        return ft;
    }

    @Override
    public int getCount() {

        return 4;
    }

}
