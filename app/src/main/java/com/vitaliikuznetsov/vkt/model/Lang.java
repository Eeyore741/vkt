package com.vitaliikuznetsov.vkt.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by vitalykuznetsov on 13/04/17.
 */

public class Lang {

    private String mCode;
    private String mTitle;

    public Lang(String code, String title){
        mCode = code;
        mTitle = title;
    }

    public String getCode() {
        return mCode;
    }

    public String getTitle() {
        return mTitle;
    }
}
