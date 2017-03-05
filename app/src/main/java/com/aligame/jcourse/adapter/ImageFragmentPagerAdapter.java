package com.aligame.jcourse.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aligame.jcourse.fragment.SwipeFragment;

/**
 * Created by lvrh on 17/3/5.
 */

public class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
    public ImageFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        return SwipeFragment.newInstance(position,getCount());
    }
}
