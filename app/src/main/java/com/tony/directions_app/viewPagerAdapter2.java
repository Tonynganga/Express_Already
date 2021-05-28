package com.tony.directions_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class viewPagerAdapter2 extends FragmentPagerAdapter {
    public viewPagerAdapter2(@NonNull FragmentManager fm) {
        super(fm);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new PPHistory();
            case 1: return new CCHistory();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return  "LOCATION TO LOCATION HISTORY";
            case 1: return  "CURRENT TO LOCATION HISTORY";
        }
        return null;
    }
}
