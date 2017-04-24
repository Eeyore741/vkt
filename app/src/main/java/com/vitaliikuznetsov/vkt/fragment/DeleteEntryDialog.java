package com.vitaliikuznetsov.vkt.fragment;


import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    public static final String ARG_ENTRY = "ARG_ENTRY";

    public DeleteEntryDialog() {
    }

    public static DeleteEntryDialog newInstance(Translation translation) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ENTRY, translation);
        DeleteEntryDialog fragment = new DeleteEntryDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_cross)
                .setTitle(R.string.alert_dialog_title)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Serializable translation = DeleteEntryDialog.this.getArguments().getSerializable(ARG_ENTRY);
                                Intent intent = new Intent();
                                intent.putExtra(ARG_ENTRY, translation);
                                DeleteEntryDialog.this.getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel, null)
                .create();
    }
}
