package com.vitaliikuznetsov.vkt.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.fragment.TranslationsTabsFragment;
import com.vitaliikuznetsov.vkt.fragment.TranslateFragment;
import com.vitaliikuznetsov.vkt.model.Event;
import com.vitaliikuznetsov.vkt.model.TranslationManager;

public class RootActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TranslateFragment translateFragment;
    private TranslationsTabsFragment translationsTabsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        TranslationManager.sharedManager.subscribe(this);
        presentTranslateFragment();
    }

    @Subscribe
    public void onBusEvent(Event event){
        switch (event.getNotification()){
            case TranslationManager.NOTIFICATION_GET_LANGUAGES:
                if (event.isSuccess()){
                    Log.d(getClass().getName(), event.getObject().toString());
                }
                else {
                    Log.d(getClass().getName(), "error");
                }
                break;
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Log.d(getClass().getName(), getSupportActionBar().toString());
            TranslationManager.sharedManager.getSupportedLanguages();
//            Lang lang = TranslationManager.sharedManager.getPreferredSourceLang();
//            if (lang != null){
//                Log.d(getClass().getName(), lang.toString());
//            }
//            else {
//                Log.d(getClass().getName(), "no source pref");
//            }
            switch (item.getItemId()) {
                case R.id.navi_translation:
                    presentTranslateFragment();
//                    toolbar.setTitle("Home");
//                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navi_favorites:
                    presentTranslationsFragment();
//                    toolbar.setTitle("Dash");
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navi_settings:
//                    toolbar.setTitle("Notif");
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    private void hideCurrentFragment(){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (currentFragment != null){
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .commit();
        }
    }

    private void presentTranslateFragment(){
        this.hideCurrentFragment();
        if (translateFragment == null){
            translateFragment = new TranslateFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, translateFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .show(translateFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
    }

    private void presentTranslationsFragment(){
        this.hideCurrentFragment();
        if (translationsTabsFragment == null){
            translationsTabsFragment = new TranslationsTabsFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, translationsTabsFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .show(translationsTabsFragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
    }
}
