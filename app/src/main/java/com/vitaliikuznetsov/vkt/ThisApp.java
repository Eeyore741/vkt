package com.vitaliikuznetsov.vkt;

import android.app.Application;

import com.vitaliikuznetsov.vkt.model.DaoSession;

/**
 * Created by vitalykuznetsov on 13/04/17.
 */

public class ThisApp extends Application {

    private static ThisApp mSharedApp;
    private DaoSession daoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        mSharedApp = this;

    }

    public static ThisApp sharedApp(){
        return mSharedApp;
    }
}
