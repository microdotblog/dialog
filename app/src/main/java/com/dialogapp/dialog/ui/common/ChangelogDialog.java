package com.dialogapp.dialog.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dialogapp.dialog.R;

public class ChangelogDialog extends DialogFragment {

    public static ChangelogDialog create(boolean darkTheme) {
        ChangelogDialog dialog = new ChangelogDialog();
        Bundle args = new Bundle();
        args.putBoolean("dark_theme", darkTheme);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView;
        try {
            customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_webview, null);
        } catch (InflateException e) {
            throw new IllegalStateException("This device does not support Web Views.");
        }
        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .theme(getArguments().getBoolean("dark_theme") ? Theme.DARK : Theme.LIGHT)
                        .title("Changelog")
                        .customView(customView, false)
                        .positiveText(R.string.dialog_dismiss)
                        .build();

        final WebView webView = customView.findViewById(R.id.webview);
        webView.loadData(
                getString(R.string.release_notes)
                        .replace(
                                "{style-placeholder}", getArguments().getBoolean("dark_theme")
                                        ? "body { background-color: #424242; color: #fff; }"
                                        : "body { background-color: #fff; color: #000; }")
                        .replace("{link-color}", toHtmlColor(getActivity(), android.R.attr.colorAccent)),
                "text/html",
                "UTF-8");
        return dialog;
    }

    private String toHtmlColor(Context context, @AttrRes int colorAttr) {
        return String.format("%06X", 0xFFFFFF & ContextCompat.getColor(context,
                getThemedResId(context, colorAttr)));
    }

    private int getThemedResId(Context context, @AttrRes int attr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        final int resId = a.getResourceId(0, 0);
        a.recycle();
        return resId;
    }
}
