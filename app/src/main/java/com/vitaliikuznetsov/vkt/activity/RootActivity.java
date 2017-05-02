package com.vitaliikuznetsov.vkt.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.fragment.SettingsFragment;
import com.vitaliikuznetsov.vkt.fragment.TranslationsTabsFragment;
import com.vitaliikuznetsov.vkt.fragment.TranslateFragment;

import java.util.ArrayList;

public class RootActivity extends AppCompatActivity {

    private TranslateFragment translateFragment;
    private TranslationsTabsFragment translationsTabsFragment;
    private SettingsFragment settingsFragment;

    public static RootActivity sharedActivity;

    ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedActivity = this;
        setContentView(R.layout.activity_root);
        fragments = new ArrayList<>();
        BottomNavigationView  bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        presentTranslateFragment();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navi_translation:

                    presentTranslateFragment();
                    return true;

                case R.id.navi_favorites:

                    presentTranslationsFragment();
                    return true;

                case R.id.navi_settings:

                    presentSettingsFragment();
                    return true;
            }
            return false;
        }

    };

    private void hideCurrentFragments(){

        for (Fragment fragment : fragments){

            getSupportFragmentManager().beginTransaction()
                    .hide(fragment)
                    .commit();
        }
    }

    private void presentTranslateFragment(){

        this.hideCurrentFragments();

        if (translateFragment == null){

            translateFragment = new TranslateFragment();
            this.fragments.add(translateFragment);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, translateFragment)
                    .commit();
        }
        else {

            getSupportFragmentManager().beginTransaction()
                    .show(translateFragment)
                    .commit();
        }
    }

    private void presentTranslationsFragment(){

        this.hideCurrentFragments();

        if (translationsTabsFragment == null){

            translationsTabsFragment = new TranslationsTabsFragment();
            this.fragments.add(translationsTabsFragment);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, translationsTabsFragment)
                    .commit();
        }
        else {

            getSupportFragmentManager().beginTransaction()
                    .show(translationsTabsFragment)
                    .commit();
        }
    }

    private void presentSettingsFragment(){

        this.hideCurrentFragments();

        if (settingsFragment == null){

            settingsFragment = new SettingsFragment();
            this.fragments.add(settingsFragment);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, settingsFragment)
                    .commit();
        }
        else {

            getSupportFragmentManager().beginTransaction()
                    .show(settingsFragment)
                    .commit();
        }
    }
}