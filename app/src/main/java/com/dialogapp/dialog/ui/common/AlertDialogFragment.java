package com.dialogapp.dialog.ui.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class AlertDialogFragment extends DialogFragment {
    private AlertDialogListener listener;

    public static AlertDialogFragment newInstance(String title, String message) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (listener != null)
                        listener.onDismissAlertDialog(true);
                    dismiss();
                });

        if (listener != null) {
            alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                listener.onDismissAlertDialog(false);
                dialog.cancel();
            });
        }

        return alertDialogBuilder.create();
    }

    public void setListener(AlertDialogListener listener) {
        this.listener = listener;
    }

    public interface AlertDialogListener {
        void onDismissAlertDialog(boolean userChoice);
    }
}
