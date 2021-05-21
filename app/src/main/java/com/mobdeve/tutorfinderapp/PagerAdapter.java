package com.mobdeve.tutorfinderapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

    private int numTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior, int numTabs) {
        super(fm, behavior);
        this.numTabs= numTabs;
    }

    @NonNull

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new CurrFragment();
            case 1:
                return new ReqFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
