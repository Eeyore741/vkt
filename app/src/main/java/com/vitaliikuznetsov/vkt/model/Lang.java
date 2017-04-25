package com.vitaliikuznetsov.vkt.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

/**
 * Created by vitalykuznetsov on 13/04/17.
 */

@Entity(indexes = {
        @Index(value = "code ASC", unique = true)
})

public class Lang implements Serializable{

    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String code;
    @NotNull
    private String title;
    private boolean preferredSource;
    private boolean preferredTarget;
    @Transient
    static final long serialVersionUID = 1;

    @Generated(hash = 1830652120)
    public Lang(Long id, String code, @NotNull String title,
            boolean preferredSource, boolean preferredTarget) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.preferredSource = preferredSource;
        this.preferredTarget = preferredTarget;
    }
    @Generated(hash = 1197397665)
    public Lang() {
    }

    public Lang(String code, @NotNull String title,
                boolean preferredSource, boolean preferredTarget) {
        this.code = code;
        this.title = title;
        this.preferredSource = preferredSource;
        this.preferredTarget = preferredTarget;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return this.code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public boolean getPreferredSource() {
        return this.preferredSource;
    }
    public void setPreferredSource(boolean preferredSource) {
        this.preferredSource = preferredSource;
    }
    public boolean getPreferredTarget() {
        return this.preferredTarget;
    }
    public void setPreferredTarget(boolean preferredTarget) {
        this.preferredTarget = preferredTarget;
    }
}