package com.vitaliikuznetsov.vkt.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.vitaliikuznetsov.vkt.R;
import com.vitaliikuznetsov.vkt.model.Translation;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteEntryDialog extends DialogFragment {

    public static final String ARG_TITLE = "ARG_TITLE";
    public static final String ARG_OBJECT = "ARG_OBJECT";

    private String title;
    private Serializable object;

    public DeleteEntryDialog() {
    }

    public static DeleteEntryDialog newInstance(String title, Translation translation) {
        Bundle args = new Bundle();
        if (title != null) args.putString(ARG_TITLE, title);
        if (translation != null) args.putSerializable(ARG_OBJECT, translation);
        DeleteEntryDialog fragment = new DeleteEntryDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null){
            this.title =  args.containsKey(ARG_TITLE) ? args.getString(ARG_TITLE) : "";
            this.object =  args.containsKey(ARG_OBJECT) ? args.getSerializable(ARG_OBJECT) : null;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_cross)
                .setTitle(title)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent();
                                if (DeleteEntryDialog.this.object != null) intent.putExtra(ARG_OBJECT, DeleteEntryDialog.this.object);
                                DeleteEntryDialog.this.getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel, null)
                .create();
    }
}
