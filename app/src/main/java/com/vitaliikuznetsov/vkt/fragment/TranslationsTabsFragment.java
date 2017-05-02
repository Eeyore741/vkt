package com.vitaliikuznetsov.vkt.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.adapter.TranslationsPagerAdapter;
import com.vitaliikuznetsov.vkt.ThisApp;
import com.vitaliikuznetsov.vkt.model.TranslationManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslationsTabsFragment extends Fragment implements View.OnClickListener{

    private static final int REQUEST_CODE_DELETE_HISTORY = 1;
    private static final int REQUEST_CODE_DELETE_FAVORITES = 2;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.buttonDelete)
    Button deleteButton;

    TranslationsPagerAdapter pagerAdapter;

    public TranslationsTabsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.pagerAdapter = new TranslationsPagerAdapter(getFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_translations_tabs, container, false);
        ButterKnife.bind(this, view);
        deleteButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.viewPager.setAdapter(this.pagerAdapter);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {

        switch (viewPager.getCurrentItem()){

            case 0:{

                String title = ThisApp.sharedApp().getResources().getString(R.string.alert_dialog_title_delete_all);
                DeleteEntryDialog deleteEntryDialog = DeleteEntryDialog.newInstance(title, null);
                deleteEntryDialog.setTargetFragment(this, REQUEST_CODE_DELETE_HISTORY);
                deleteEntryDialog.show(getFragmentManager(), null);
            }
                break;

            case 1:

                String title = ThisApp.sharedApp().getResources().getString(R.string.alert_dialog_title_delete_all);
                DeleteEntryDialog deleteEntryDialog = DeleteEntryDialog.newInstance(title, null);
                deleteEntryDialog.setTargetFragment(this, REQUEST_CODE_DELETE_FAVORITES);
                deleteEntryDialog.show(getFragmentManager(), null);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case REQUEST_CODE_DELETE_HISTORY:{

                if (resultCode == Activity.RESULT_OK){

                    TranslationManager.sharedManager.deleteAllHistory();
                }
            }
            break;

            case REQUEST_CODE_DELETE_FAVORITES:{

                if (resultCode == Activity.RESULT_OK){

                    TranslationManager.sharedManager.deleteAllFavorites();
                }
            }
            break;
        }
    }
}
