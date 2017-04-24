package com.vitaliikuznetsov.vkt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.fragment.TranslationsListFragment;
import com.vitaliikuznetsov.vkt.model.ThisApp;

/**
 * Created by VItalii on 21/04/2017.
 */

public class TranslationsPagerAdapter extends FragmentPagerAdapter {

    public TranslationsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return TranslationsListFragment.newInstance(TranslationsListFragment.Mode.History);
            case 1: return TranslationsListFragment.newInstance(TranslationsListFragment.Mode.Favorite);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return ThisApp.sharedApp().getResources().getString(R.string.tab_title_history);
            case 1: return ThisApp.sharedApp().getResources().getString(R.string.tab_title_favorite);
        }
        return null;
    }
}