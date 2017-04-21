package com.vitaliikuznetsov.vkt.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vitaliikuznetsov.vkt.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TranslationsListFragment extends Fragment {

    public static final String ARG_MODE = "ARG_MODE";

    public enum Mode {History, Favorite};

    private Mode mode;

    @BindView(R.id.tv)
    TextView tv;

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
        if (args != null){
            if (args.containsKey(ARG_MODE)) this.mode = (Mode) args.getSerializable(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translations_list, container, false);
        ButterKnife.bind(this, view);
        switch (this.mode){
            case History:
                tv.setText("history");
                break;
            case Favorite:
                tv.setText("favorite");
                break;
        }
        return view;
    }

}
