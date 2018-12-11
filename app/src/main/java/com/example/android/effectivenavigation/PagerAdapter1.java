package com.example.android.effectivenavigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class PagerAdapter1 extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ViewPager viewp;

    public PagerAdapter1(FragmentManager fm, int NumOfTabs, ViewPager viewp) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.viewp = viewp;
        //viewp.setOffscreenPageLimit(4);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new InnerMyListFragment(viewp);
            case 1:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                return new Main2Activity.TinderSectionFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}