package com.vitaliikuznetsov.vkt.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.adapter.TranslationsAdapter;
import com.vitaliikuznetsov.vkt.model.Event;
import com.vitaliikuznetsov.vkt.model.ThisApp;
import com.vitaliikuznetsov.vkt.model.Translation;
import com.vitaliikuznetsov.vkt.model.TranslationManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslationsListFragment extends Fragment implements TranslationsAdapter.TranslationsAdapterListener {

    public static final String ARG_MODE = "ARG_MODE";

    public static final int REQUEST_CODE_DELETE_ENTRY = 1;

    public static final int COUNTDOWN_TIMER_DELAY = 1000;

    public enum Mode {History, Favorite};

    private Mode mode;
    private CountDownTimer countDownTimer;
    private List<Translation> translations;

    @BindView(R.id.editSearch)
    EditText searchEdit;
    @BindView(R.id.imageCross)
    ImageView crossImage;
    @BindView(R.id.recycleView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.lineGray)
    View grayLine;
    @BindView(R.id.textEmpty)
    TextView emptyText;

    public TranslationsListFragment() {
    }

    public static TranslationsListFragment newInstance(Mode mode) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        TranslationsListFragment fragment = new TranslationsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_MODE)){
            this.mode = (Mode) args.getSerializable(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translations_list, container, false);
        ButterKnife.bind(this, view);
        String hint;
        switch (this.mode){
            case History:
                hint = ThisApp.sharedApp().getResources().getString(R.string.hint_search_history);
                searchEdit.setHint(hint);
                break;
            case Favorite:
                hint = ThisApp.sharedApp().getResources().getString(R.string.hint_search_favorite);
                searchEdit.setHint(hint);
                break;
        }
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TranslationsListFragment.this.setClearEnabled(editable.length() > 0);
                TranslationsListFragment.this.startDataLoadCountdown();
            }
        });
        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslationsListFragment.this.searchEdit.setText("");
                TranslationsListFragment.this.loadData();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setProgressHidden(true);
        setClearEnabled(false);
        setEmptyHidden(true);
        TranslationManager.sharedManager.subscribe(this);
        loadData();
    }

    @Override
    public void onDestroyView() {
        TranslationManager.sharedManager.unsubscribe(this);
        super.onDestroyView();
    }

    @Subscribe
    public void onBusEvent(Event event){

        switch (this.mode){

            case History:{

                switch (event.getNotification()){

                    case TranslationManager.NOTIFICATION_SELECT_TRANSLATIONS:{
                        setProgressHidden(true);
                        if (event.isSuccess()){
                            translations = (List< Translation>) event.getObject();
                            if (translations.size() > 0){
                                TranslationsAdapter adapter = new TranslationsAdapter(translations, this);
                                this.recyclerView.setAdapter(adapter);
                                this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                this.setEmptyHidden(true);
                            }
                            else {
                                this.setEmptyHidden(false);
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), "Ошибка загрузки", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;

                    case TranslationManager.NOTIFICATION_TRANSLATE:{
                        if (event.isSuccess()
                                && searchEdit.getText().length() == 0) {
                            this.loadData();
                        }
                    }
                    break;

                    case TranslationManager.NOTIFICATION_DELETE_TRANSLATION:{
                        if (event.isSuccess()){
                            Translation translation = (Translation) event.getObject();
                            if (this.translations.contains(translation)){
                                recyclerView.getAdapter().notifyItemRemoved(this.translations.indexOf(translation));
                                this.translations.remove(translation);
                            }
                        }
                    }
                    break;

                    case TranslationManager.NOTIFICATION_UPDATE_TRANSLATION:{
                        if (event.isSuccess()){
                            Translation translation = (Translation) event.getObject();
                            if (this.translations.contains(translation)){
                                recyclerView.getAdapter().notifyItemChanged(this.translations.indexOf(translation));
                            }
                        }
                    }
                    break;
                }
            }
            break;

            case Favorite:

                switch (event.getNotification()){

                    case TranslationManager.NOTIFICATION_SELECT_FAVORITE_TRANSLATIONS:{
                        setProgressHidden(true);
                        if (event.isSuccess()){
                            translations = (List< Translation>) event.getObject();
                            if (translations.size() > 0){
                                TranslationsAdapter adapter = new TranslationsAdapter(translations, this);
                                this.recyclerView.setAdapter(adapter);
                                this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                this.setEmptyHidden(true);
                            }
                            else {
                                this.setEmptyHidden(false);
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), "Ошибка загрузки", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;

                    case TranslationManager.NOTIFICATION_UPDATE_TRANSLATION:{
                        if (event.isSuccess()){
                            Translation translation = (Translation) event.getObject();
                            if (this.translations.contains(translation)
                                    && !translation.getFavorite()){
                                recyclerView.getAdapter().notifyItemRemoved(this.translations.indexOf(translation));
                                this.translations.remove(translation);
                            }
                            else
                            if (!this.translations.contains(translation)
                                    && translation.getFavorite()){
                                this.translations.add(0, translation);
                                recyclerView.getAdapter().notifyItemInserted(0);
                                recyclerView.scrollToPosition(0);
                            }
                        }
                    }
                    break;

                    case TranslationManager.NOTIFICATION_DELETE_TRANSLATION:{
                        if (event.isSuccess()){
                            Translation translation = (Translation) event.getObject();
                            if (this.translations.contains(translation)){
                                recyclerView.getAdapter().notifyItemRemoved(this.translations.indexOf(translation));
                                this.translations.remove(translation);
                            }
                        }
                    }
                    break;
                }
                break;
        }
    }

    @Override
    public void onTranslationClick(Translation translation) {

    }

    @Override
    public void onTranslationLongClick(Translation translation) {
        DeleteEntryDialog deleteEntryDialog = DeleteEntryDialog.newInstance(translation);
        deleteEntryDialog.setTargetFragment(this, REQUEST_CODE_DELETE_ENTRY);
        deleteEntryDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onTranslationFavoriteClick(Translation translation) {
        TranslationManager.sharedManager.updateTranslationFavorite(translation, !translation.getFavorite());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_DELETE_ENTRY){
            if (resultCode == Activity.RESULT_OK){
                if (data.hasExtra(DeleteEntryDialog.ARG_ENTRY)){
                    Translation translation = (Translation) data.getSerializableExtra(DeleteEntryDialog.ARG_ENTRY);
                    TranslationManager.sharedManager.deleteTranslation(translation);
                }
            }
        }
    }

    private void setProgressHidden(boolean hidden){
        progressBar.setVisibility(hidden ? View.INVISIBLE : View.VISIBLE);
        grayLine.setVisibility(hidden ? View.VISIBLE : View.INVISIBLE);
    }

    private void setClearEnabled(boolean enabled){
        crossImage.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    private void setEmptyHidden(boolean hidden){
        emptyText.setVisibility(hidden ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(hidden ? View.VISIBLE : View.GONE);
    }

    private void startDataLoadCountdown(){
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(COUNTDOWN_TIMER_DELAY, COUNTDOWN_TIMER_DELAY) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                TranslationsListFragment.this.loadData();
            }
        };
        countDownTimer.start();
    }

    private void loadData(){
        setProgressHidden(false);
        switch (this.mode){
            case History:
                TranslationManager.sharedManager.selectTranslationsWithString(searchEdit.getText().toString());
                break;
            case Favorite:
                TranslationManager.sharedManager.selectFavoriteTranslationsWithString(searchEdit.getText().toString());
                break;
        }
    }
}