package com.vitaliikuznetsov.vkt.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.fragment.TranslateFragment;
import com.vitaliikuznetsov.vkt.model.Event;
import com.vitaliikuznetsov.vkt.model.Lang;
import com.vitaliikuznetsov.vkt.model.TranslationManager;

import java.util.Locale;

public class RootActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TranslateFragment mTranslateFragment;
    private Locale mLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        showTranslateFragment();
        TranslationManager.sharedManager.subscribe(this);

//        Locale locale = getCurrentLocale();
//        Log.d(RootActivity.class.getName(), locale.toString());
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
                case R.id.navigation_home:
//                    toolbar.setTitle("Home");
//                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
//                    toolbar.setTitle("Dash");
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
//                    toolbar.setTitle("Notif");
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    private void showTranslateFragment(){
        if (mTranslateFragment == null) mTranslateFragment = new TranslateFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, mTranslateFragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }
//
//    public Locale getCurrentLocale(){
//        if (mLocale == null){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                mLocale = getResources().getConfiguration().getLocales().get(0);
//            } else{
//                mLocale = getResources().getConfiguration().locale;
//            }
//        }
//        return mLocale;
//    }
}
