package com.vitaliikuznetsov.vkt.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.model.Lang;

import java.security.PrivateKey;
import java.util.List;

/**
 * Created by VItalii on 17/04/2017.
 */

public class LangsAdapter extends ArrayAdapter {

    private Lang selectedLang;
    private Drawable greenCheck;

    static public class LangViewHolder{

        TextView titleLang;
        ImageView checkImage;
    }

    public LangsAdapter(@NonNull Activity context, @NonNull List objects, Lang lang) {
        super(context, R.layout.row_lang, objects);

        this.selectedLang = lang;
        greenCheck = ContextCompat.getDrawable(getContext(), R.drawable.ic_check_green);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LangViewHolder langViewHolder = convertView != null ? (LangViewHolder) convertView.getTag() : new LangViewHolder();

        if (convertView == null){

            Activity activity = (Activity) getContext();
            convertView = activity.getLayoutInflater().inflate(R.layout.row_lang, parent, false);
            langViewHolder.titleLang = (TextView) convertView.findViewById(R.id.textView);
            langViewHolder.checkImage = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(langViewHolder);
        }
        Lang lang = (Lang) getItem(position);
        langViewHolder.titleLang.setText(lang.getTitle());
        langViewHolder.checkImage.setImageDrawable( (selectedLang != null && selectedLang.getCode().equals(lang.getCode())) ? greenCheck : null);

        return convertView;
    }
}