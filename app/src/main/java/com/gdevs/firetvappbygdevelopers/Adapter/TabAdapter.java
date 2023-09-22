package com.gdevs.firetvappbygdevelopers.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.gdevs.firetvappbygdevelopers.Fragment.CategoryFragment;
import com.gdevs.firetvappbygdevelopers.Fragment.LatestFragment;


public class TabAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public TabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LatestFragment homeFragment = new LatestFragment();
                return homeFragment;
            case 1:
                CategoryFragment photoFragment = new CategoryFragment();
                return photoFragment;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}
