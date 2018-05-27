package com.dialogapp.dialog.ui.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.common.RequestViewModel;
import com.dialogapp.dialog.util.NetworkUtil;
import com.dialogapp.dialog.util.SharedPrefUtil;

import javax.inject.Inject;

public abstract class BaseListActivity extends BaseNetworkWatcherActivity implements
        BaseListFragment.FragmentEventListener {

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;

    @Inject
    protected SharedPrefUtil sharedPrefUtil;

    protected RequestViewModel requestViewModel;

    private Snackbar errorBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestViewModel = ViewModelProviders.of(this, viewModelFactory).get(RequestViewModel.class);
        requestViewModel.getResponseFavorite().observe(this, booleanResource -> {
            if (booleanResource.getContentIfNotHandled() != null) {
                if (!booleanResource.peekContent().data) {
                    Toast.makeText(this, R.string.request_unsuccessful, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onLoadSuccess(Object data) {

    }

    @Override
    public void onLoadError(String message) {
        if (errorBar != null) {
            if (NetworkUtil.getConnectivityStatus(this) == NetworkUtil.TYPE_NOT_CONNECTED) {
                errorBar.setText("You are currently offline").setAction("", null);
            } else {
                errorBar.setText(R.string.connection_error)
                        .setAction("Show error", view -> {
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

    @Override
    public void onFavoriteButtonClicked(String postId, boolean state) {
        requestViewModel.setFavoriteState(postId, state);
    }

    @Override
    public ViewModelProvider.Factory getViewModelFactory() {
        return viewModelFactory;
    }

    @Override
    public LiveData<Boolean> getConnection() {
        return connectionViewModel.getConnectionStatus();
    }

    @Override
    public boolean shouldColorUsernameLinks() {
        return sharedPrefUtil.getBooleanPreference(getString(R.string.pref_color_username_links), false);
    }

    protected void setErrorBar(CoordinatorLayout coordinatorLayout) {
        errorBar = Snackbar.make(coordinatorLayout, R.string.connection_error, Snackbar.LENGTH_LONG);
    }

    protected boolean isUserSelf(String username) {
        return getSavedUsername().equals(username);
    }

    protected String getSavedUsername() {
        return sharedPrefUtil.getStringPreference(getString(R.string.pref_username), "");
    }

    protected String getSavedAvatarUrl() {
        return sharedPrefUtil.getStringPreference(getString(R.string.pref_avatar_url), "");
    }

    protected String getSavedFullname() {
        return sharedPrefUtil.getStringPreference(getString(R.string.pref_fullname), "");
    }
}
