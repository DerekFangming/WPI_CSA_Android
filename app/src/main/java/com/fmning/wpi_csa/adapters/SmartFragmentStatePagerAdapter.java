package com.fmning.wpi_csa.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by fangmingning
 * On 10/31/17.
 */

public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @SuppressWarnings("unused")
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}