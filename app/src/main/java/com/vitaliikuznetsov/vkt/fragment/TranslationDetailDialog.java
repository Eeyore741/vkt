package com.vitaliikuznetsov.vkt.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.model.Translation;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */

public class TranslationDetailDialog extends DialogFragment {

    public static final String ARG_TRANSLATION = "ARG_TRANSLATION";

    private Translation translation;

    @BindView(R.id.textSource)
    TextView sourceText;
    @BindView(R.id.textTarget)
    TextView targetText;

    public TranslationDetailDialog() {
    }

    public static TranslationDetailDialog newInstance(Translation translation) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_TRANSLATION, translation);
        TranslationDetailDialog fragment = new TranslationDetailDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null){

            if (args.containsKey(ARG_TRANSLATION)) this.translation = (Translation) args.getSerializable(ARG_TRANSLATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_translation_detail_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String title = translation.getSourceLangCode().toUpperCase() + "-" + translation.getSourceLangCode().toUpperCase();
        getDialog().setTitle(title);

        sourceText.setText(translation.getText());
        targetText.setText(translation.getTranslation());
    }
}
