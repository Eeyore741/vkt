package com.vitaliikuznetsov.vkt.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.model.Translation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by VItalii on 23/04/2017.
 */

public class TranslationsAdapter extends RecyclerView.Adapter {

        public interface TranslationsAdapterListener {
            void onTranslationClick(Translation translation);
            void onTranslationFavoriteClick(Translation translation);
            void onTranslationLongClick(Translation translation);
        }

        static class TranslationViewHolder extends RecyclerView.ViewHolder{

            @BindView(R.id.imageHeart)
            ImageView heartImage;
            @BindView(R.id.textSource)
            TextView sourceText;
            @BindView(R.id.textTarget)
            TextView targetText;
            @BindView(R.id.textCode)
            TextView codeText;
            @BindView(R.id.buttonHeart)
            Button heartButton;

            public TranslationViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    private List<Translation> translations;
    private TranslationsAdapterListener listener;

    public TranslationsAdapter(List<Translation> translations, TranslationsAdapterListener listener) {
        this.translations = translations;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return translations.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View translationView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_translation_item, parent, false);
        return new TranslationViewHolder(translationView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TranslationViewHolder tViewHolder = (TranslationViewHolder) holder;
        final Translation translation = translations.get(position);
        String codeTest = translation.getLangCode().toUpperCase();
        tViewHolder.codeText.setText(codeTest);
        tViewHolder.heartImage.setImageResource(translation.getFavorite() ? R.drawable.ic_heart_blue : R.drawable.ic_heart_gray);
        tViewHolder.sourceText.setText(translation.getText());
        tViewHolder.targetText.setText(translation.getTranslation());
        tViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslationsAdapter.this.listener.onTranslationClick(translation);
            }
        });
        tViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TranslationsAdapter.this.listener.onTranslationLongClick(translation);
                return true;
            }
        });
        tViewHolder.heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslationsAdapter.this.listener.onTranslationFavoriteClick(translation);
            }
        });
    }
}
