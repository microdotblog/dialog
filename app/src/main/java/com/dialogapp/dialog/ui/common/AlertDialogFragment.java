package com.dialogapp.dialog.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class AlertDialogFragment extends DialogFragment {
    private AlertDialogListener listener;

    public static AlertDialogFragment newInstance(String title, String message, boolean negativeButton) {
        AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putBoolean("negative", negativeButton);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (AlertDialogListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement AlertDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        boolean negativeButton = getArguments().getBoolean("negative");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);

        if (negativeButton) {
            alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                listener.onFinishAlertDialog(true);
            });

            alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                if (dialog != null) {
                    listener.onFinishAlertDialog(false);
                    dialog.cancel();
                }
            });
        } else {
            alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> {
                dismiss();
            });
        }

        return alertDialogBuilder.create();
    }

    public interface AlertDialogListener {
        void onFinishAlertDialog(boolean userChoice);
    }
}
