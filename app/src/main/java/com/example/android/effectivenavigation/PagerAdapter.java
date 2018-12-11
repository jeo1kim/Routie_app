package com.example.android.effectivenavigation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    ViewPager viewp;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, ViewPager viewp) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.viewp = viewp;
        //viewp.setOffscreenPageLimit(2);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
               // this.notifyDataSetChanged();
                return new SearchSectionFragment(viewp);
                //return tab1;
            case 1:
                //this.notifyDataSetChanged();
                return new MyLocationsListFragment(viewp, this);
            case 2:
                return new ProfileFragment();

            case 3:
                return new Main2Activity.Option();//.DummySectionFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}