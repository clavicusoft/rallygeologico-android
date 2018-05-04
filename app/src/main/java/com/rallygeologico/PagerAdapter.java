package com.rallygeologico;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class PagerAdapter extends FragmentPagerAdapter {

    private static int NUM_PAGES = 2;

    Context mContext;
    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle args;
        switch (position) {
            case 0:
                fragment = new RallyListFragment();
                args = new Bundle();
                fragment.setArguments(args);
                return fragment;
            case 1:
                fragment = new AchievementListFragment();
                args = new Bundle();
                fragment.setArguments(args);
                return fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Rallies";
            case 1:
                return "Logros";
        }

        return null;
    }
}
