package com.project_test.forsix;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by kun on 2017/1/14.
 */

public class SplashAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;

    public SplashAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (mFragmentList != null) {
            ret = mFragmentList.size();
        }
        return ret;
    }
}
