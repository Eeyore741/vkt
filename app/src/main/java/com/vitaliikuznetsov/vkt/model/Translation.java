package com.vitaliikuznetsov.vkt.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.sql.SQLOutput;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by VItalii on 19/04/2017.
 */

@Entity()

public class Translation implements Serializable {

    @Id(autoincrement = true)
    private Long id;
    @Unique
    private int hash;
    @NotNull
    private String langCode;
    @NotNull
    private String text;
    private String translation;
    private boolean favorite;
    @Transient
    static final long serialVersionUID = 1;

    @Keep
    public Translation(String langCode, String text) {
        this.langCode = langCode;
        this.text = text;
        this.hash = langCode.hashCode() ^ text.hashCode();
    }

    @Keep
    public static int hash(String sourceLangCode, String targetLangCode, String text){

        if (sourceLangCode != null
                && targetLangCode != null
                && text != null){

            return (sourceLangCode + "-" + targetLangCode).hashCode() ^ text.hashCode();
        }
        return 0;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getHash() {
        return this.hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public String getLangCode() {
        return this.langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslation() {
        return this.translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public boolean getFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Generated(hash = 1919046464)
    public Translation(Long id, int hash, @NotNull String langCode, @NotNull String text,
            String translation, boolean favorite) {
        this.id = id;
        this.hash = hash;
        this.langCode = langCode;
        this.text = text;
        this.translation = translation;
        this.favorite = favorite;
    }

    @Generated(hash = 321689573)
    public Translation() {
    }
}
