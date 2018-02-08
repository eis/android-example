package com.github.eis.myandroidapp.slide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A simple pager adapter that represents 5 ScreenSlidePagerFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    final int NUM_PAGES = 5;

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ScreenSlidePagerFragment fragment = new ScreenSlidePagerFragment();
        fragment.setText("position " + (position+1));
        return fragment;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

}
