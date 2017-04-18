package com.vitaliikuznetsov.vkt.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.activity.RootActivity;
import com.vitaliikuznetsov.vkt.adapter.LangsAdapter;
import com.vitaliikuznetsov.vkt.model.Event;
import com.vitaliikuznetsov.vkt.model.Lang;
import com.vitaliikuznetsov.vkt.model.TranslationManager;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslateFragment extends Fragment {

    public static final int REQUEST_CODE_SOURCE_LANG = 1;
    public static final int REQUEST_CODE_TARGET_LANG = 2;

    @BindView(R.id.textLangSource)
    TextView sourceLangText;
    @BindView(R.id.textLangTarget)
    TextView targetLangText;
    @BindView(R.id.buttonSwap)
    Button directionSwapButton;
    @BindView(R.id.editInput)
    EditText inputEdit;
    @BindView(R.id.buttonClear)
    Button clearButton;
    @BindView(R.id.textOutput)
    TextView outputText;
    @BindView(R.id.buttonSave)
    Button saveButton;
    @BindView(R.id.imageTrash)
    ImageView imageTrash;
    @BindView(R.id.imageFloppy)
    ImageView imageFloppy;

    private Lang sourceLang;
    private Lang targetLang;
    private CountDownTimer countDownTimer;

    public TranslateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        ButterKnife.bind(this, view);
        sourceLangText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LangListFragment langListFragment = LangListFragment.newInstance(sourceLang);
                langListFragment.setTargetFragment(TranslateFragment.this, REQUEST_CODE_SOURCE_LANG);
                langListFragment.show(TranslateFragment.this.getFragmentManager(), null);
            }
        });
        targetLangText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LangListFragment langListFragment = LangListFragment.newInstance(targetLang);
                langListFragment.setTargetFragment(TranslateFragment.this, REQUEST_CODE_TARGET_LANG);
                langListFragment.show(TranslateFragment.this.getFragmentManager(), null);
            }
        });
        directionSwapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Lang sourceLang = TranslateFragment.this.sourceLang;
                Lang targetLang = TranslateFragment.this.targetLang;
                TranslateFragment.this.setSourceLang(targetLang);
                TranslateFragment.this.setTargetLang(sourceLang);
                TranslationManager.sharedManager.setPreferredSourceLang(TranslateFragment.this.sourceLang);
                TranslationManager.sharedManager.setPreferredTargetLang(TranslateFragment.this.targetLang);
            }
        });
        inputEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                setCleaningEnabled(inputEdit.getText().length() > 0);
                TranslateFragment.this.startTranslateCountdown(keyEvent.getEventTime());
                return false;
            }
        });
        inputEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                setCleaningEnabled(inputEdit.getText().length() > 0);
                TranslateFragment.this.startTranslateCountdown(keyEvent.getEventTime());
                return false;
            }
        });
        setSavingEnabled(false);
        setCleaningEnabled(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Lang sourceLang = TranslationManager.sharedManager.getPreferredSourceLang();
        if (sourceLang != null) setSourceLang(sourceLang);
        Lang targetLang = TranslationManager.sharedManager.getPreferredTargetLang();
        if (targetLang != null) setTargetLang(targetLang);
        TranslationManager.sharedManager.subscribe(this);
        TranslationManager.sharedManager.getSupportedLanguages();
    }

    @Override
    public void onDestroyView() {
        TranslationManager.sharedManager.unsubscribe(this);
        super.onDestroyView();
    }

    private void startTranslateCountdown(final long eventTime){
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SOURCE_LANG:
                if (resultCode == Activity.RESULT_OK){
                    Lang resultLang = (Lang) data.getSerializableExtra(LangListFragment.ARG_SELECTED_LANG);
                    if (resultLang.equals(targetLang)){
                        setTargetLang(sourceLang);
                    }
                    setSourceLang(resultLang);
                    TranslationManager.sharedManager.setPreferredSourceLang(sourceLang);
                }
                break;
            case REQUEST_CODE_TARGET_LANG:
                if (resultCode == Activity.RESULT_OK){
                    Lang resultLang = (Lang) data.getSerializableExtra(LangListFragment.ARG_SELECTED_LANG);
                    if (resultLang.equals(sourceLang)){
                        setSourceLang(targetLang);
                    }
                    setTargetLang(resultLang);
                    TranslationManager.sharedManager.setPreferredTargetLang(targetLang);
                }
                break;
        }
    }

    private void setSourceLang(Lang lang){
        sourceLang = lang;
        sourceLangText.setText(sourceLang.getTitle());
    }

    private void setTargetLang(Lang lang){
        targetLang = lang;
        targetLangText.setText(targetLang.getTitle());
    }

    private void setCleaningEnabled(boolean enabled){
        clearButton.setEnabled(enabled);
        imageTrash.setImageAlpha(enabled ? 255 : 70);
    }

    private void setSavingEnabled(boolean enabled){
        saveButton.setEnabled(enabled);
        imageFloppy.setImageAlpha(enabled ? 255 : 70);
    }

    @Subscribe
    public void onBusEvent(Event event){
        if (event.getNotification() == TranslationManager.NOTIFICATION_GET_LANGUAGES){
            if (event.isSuccess()){
                Lang sourceLang = TranslationManager.sharedManager.getPreferredSourceLang();
                if (sourceLang != null) setSourceLang(sourceLang);
                Lang targetLang = TranslationManager.sharedManager.getPreferredTargetLang();
                if (targetLang != null) setTargetLang(targetLang);
            }
        }
    }
}
