package com.vitaliikuznetsov.vkt.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.model.Event;
import com.vitaliikuznetsov.vkt.model.Lang;
import com.vitaliikuznetsov.vkt.model.Translation;
import com.vitaliikuznetsov.vkt.model.TranslationManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslateFragment extends Fragment {

    public static final int REQUEST_CODE_SOURCE_LANG = 1;
    public static final int REQUEST_CODE_TARGET_LANG = 2;

    public static final int COUNTDOWN_TIMER_DELAY = 1000;

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
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Lang sourceLang;
    private Lang targetLang;
    private CountDownTimer countDownTimer;
    private Translation currentTranslation;

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
                if (countDownTimer != null) countDownTimer.cancel();
                if (outputText.getText().length() > 0){
                    inputEdit.setText(outputText.getText());
                    outputText.setText(null);
                    TranslateFragment.this.beginTranslation();
                }
            }
        });
        inputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (inputEdit.getText().length() > 0){
                    TranslateFragment.this.setCleaningEnabled(true);
                    TranslateFragment.this.startTranslateCountdown();
                }
                else {
                    TranslateFragment.this.cancelTranslation();
                    outputText.setText(null);
                    TranslateFragment.this.setSavingEnabled(false);
                    TranslateFragment.this.setCleaningEnabled(false);
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslateFragment.this.cancelTranslation();
                TranslateFragment.this.resetTranslation();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TranslateFragment.this.currentTranslation != null)
                    TranslationManager.sharedManager.updateTranslationFavorite(TranslateFragment.this.currentTranslation, true);
            }
        });
        this.resetTranslation();
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
                    beginTranslation();
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
                    beginTranslation();
                }
            break;
        }
    }

    @Subscribe
    public void onBusEvent(Event event){

        switch (event.getNotification()){

            case TranslationManager.NOTIFICATION_GET_LANGUAGES:{
                if (event.isSuccess()){
                    Lang sourceLang = TranslationManager.sharedManager.getPreferredSourceLang();
                    if (sourceLang != null) setSourceLang(sourceLang);
                    Lang targetLang = TranslationManager.sharedManager.getPreferredTargetLang();
                    if (targetLang != null) setTargetLang(targetLang);
                }
            }
            break;

            case TranslationManager.NOTIFICATION_TRANSLATE:{
                progressBar.setVisibility(View.INVISIBLE);
                if (event.isSuccess()){
                    currentTranslation = (Translation) event.getObject();
                    String currentText = String.valueOf(inputEdit.getText());
                    if (currentText != null
                            && currentTranslation.getText().equals(currentText)){
                        outputText.setText(currentTranslation.getTranslation());
                        setSavingEnabled(!currentTranslation.getFavorite());
                    }
                }
            }
            break;

            case TranslationManager.NOTIFICATION_UPDATE_TRANSLATION:{
                if (event.isSuccess()){
                    Translation translation = (Translation) event.getObject();
                    if (currentTranslation != null
                            && translation.equals(currentTranslation)){
                        setSavingEnabled(!translation.getFavorite());
                    }
                }
            }
            break;

            case TranslationManager.NOTIFICATION_DELETE_TRANSLATION:{
                if (event.isSuccess()){
                    Translation translation = (Translation) event.getObject();
                    if (currentTranslation != null
                            && translation.equals(currentTranslation)){
                        setSavingEnabled(true);
                    }
                }
            }
            break;
        }
    }

    private void startTranslateCountdown(){
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(COUNTDOWN_TIMER_DELAY, COUNTDOWN_TIMER_DELAY) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                TranslateFragment.this.beginTranslation();
            }
        };
        countDownTimer.start();
    }

    private void beginTranslation(){
        String text = String.valueOf(inputEdit.getText());
        if (text != null && text.length() > 0){
            this.progressBar.setVisibility(View.VISIBLE);
            TranslationManager.sharedManager.getTranslation(text, sourceLang, targetLang);
        }
    }

    private void resetTranslation(){
        inputEdit.setText(null);
        outputText.setText(null);
        TranslateFragment.this.setSavingEnabled(false);
        TranslateFragment.this.setCleaningEnabled(false);
    }

    private void cancelTranslation(){
        this.progressBar.setVisibility(View.INVISIBLE);
        if (countDownTimer != null) countDownTimer.cancel();
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
}