package com.vitaliikuznetsov.vkt.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.adapter.LangsAdapter;
import com.vitaliikuznetsov.vkt.model.Event;
import com.vitaliikuznetsov.vkt.model.Lang;
import com.vitaliikuznetsov.vkt.model.TranslationManager;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LangListFragment extends DialogFragment {

    public static final String ARG_SELECTED_LANG = "ARG_SELECTED_LANG";

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.listView)
    ListView listView;

    private Lang selectedLang;

    public static LangListFragment newInstance(Lang lang) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SELECTED_LANG, lang);
        LangListFragment fragment = new LangListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LangListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            if (bundle.containsKey(ARG_SELECTED_LANG)) this.selectedLang = (Lang) bundle.getSerializable(ARG_SELECTED_LANG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lang_list, container, false);
        ButterKnife.bind(this, view);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Lang lang = (Lang) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent();
                intent.putExtra(ARG_SELECTED_LANG, lang);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                LangListFragment.this.dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        progressBar.setVisibility(View.VISIBLE);
        getDialog().setTitle("Выберите язык");
        TranslationManager.sharedManager.subscribe(this);
        TranslationManager.sharedManager.getSupportedLanguages();
    }

    @Override
    public void onDestroyView() {
        TranslationManager.sharedManager.unsubscribe(this);
        super.onDestroyView();
    }

    @Subscribe
    public void onBusEvent(Event event){
        if (event.getNotification() == TranslationManager.NOTIFICATION_GET_LANGUAGES){
            progressBar.setVisibility(View.INVISIBLE);
            if (event.isSuccess()){
                List<Lang> langs = (List<Lang>) event.getObject();
                LangsAdapter langsAdapter = new LangsAdapter(getActivity(), langs, selectedLang);
                listView.setAdapter(langsAdapter);
            }
            else {
                Toast.makeText(getActivity(), event.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
