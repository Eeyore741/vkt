package com.vitaliikuznetsov.vkt.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
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
    private String sourceLangCode;
    @NotNull
    private String targetLangCode;
    @NotNull
    private String text;
    private String translation;
    private boolean favorite;
    @Transient
    static final long serialVersionUID = 1;

    @Keep
    public static int hash(String sourceLangCode, String targetLangCode, String text){
        return sourceLangCode.hashCode() ^ targetLangCode.hashCode() ^ text.hashCode();
    }

    @Keep
    public Translation(String sourceLangCode, String targetLangCode, String text) {
        this.sourceLangCode = sourceLangCode;
        this.targetLangCode = targetLangCode;
        this.text = text;
        this.hash = sourceLangCode.hashCode() ^ targetLangCode.hashCode() ^ text.hashCode();
    }

    @Generated(hash = 1173735606)
    public Translation(Long id, int hash, @NotNull String sourceLangCode,
            @NotNull String targetLangCode, @NotNull String text, String translation,
            boolean favorite) {
        this.id = id;
        this.hash = hash;
        this.sourceLangCode = sourceLangCode;
        this.targetLangCode = targetLangCode;
        this.text = text;
        this.translation = translation;
        this.favorite = favorite;
    }

    @Generated(hash = 321689573)
    public Translation() {
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

    public String getSourceLangCode() {
        return this.sourceLangCode;
    }

    public void setSourceLangCode(String sourceLangCode) {
        this.sourceLangCode = sourceLangCode;
    }

    public String getTargetLangCode() {
        return this.targetLangCode;
    }

    public void setTargetLangCode(String targetLangCode) {
        this.targetLangCode = targetLangCode;
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
}
