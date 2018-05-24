package com.dialogapp.dialog.ui.base;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.util.NetworkUtil;

public abstract class BaseListActivity extends BaseNetworkWatcherActivity implements
        BaseListFragment.FragmentEventListener {

    private Snackbar errorBar;

    @Override
    public void onLoadSuccess(Object data) {

    }

    @Override
    public void onLoadError(String message) {
        if (errorBar != null) {
            if (NetworkUtil.getConnectivityStatus(this) == NetworkUtil.TYPE_NOT_CONNECTED) {
                errorBar.setText("You are currently offline");
            } else {
                errorBar.setAction("Show error", view -> {
                    new MaterialDialog.Builder(this)
                            .title("Connection Error")
                            .content(message)
                            .positiveText(R.string.dialog_dismiss)
                            .show();
                });
            }
            errorBar.show();
        }
    }

    protected void setErrorBar(CoordinatorLayout coordinatorLayout) {
        errorBar = Snackbar.make(coordinatorLayout, R.string.connection_error, Snackbar.LENGTH_LONG);
    }
}
