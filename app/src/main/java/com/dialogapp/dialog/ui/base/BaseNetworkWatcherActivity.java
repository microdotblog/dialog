package com.dialogapp.dialog.ui.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.annotation.Nullable;

import timber.log.Timber;

public abstract class BaseNetworkWatcherActivity extends BaseInjectableActivity {
    public ConnectionViewModel connectionViewModel;

    private ConnectivityManager manager;
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            connectionViewModel.setConnectionStatus(true);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            connectionViewModel.setConnectionStatus(manager != null && (manager.getActiveNetworkInfo() != null));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        connectionViewModel = ViewModelProviders.of(this).get(ConnectionViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
        connectionViewModel.setConnectionStatus(manager.getActiveNetworkInfo() != null);
        Timber.d("Registered network callback");
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.unregisterNetworkCallback(networkCallback);
        Timber.d("Unregistered network callback");
    }

    public static class ConnectionViewModel extends ViewModel {
        private MutableLiveData<Boolean> isConnected = new MutableLiveData<>();

        public LiveData<Boolean> getConnectionStatus() {
            return isConnected;
        }

        public void setConnectionStatus(Boolean isConnected) {
            this.isConnected.postValue(isConnected);
        }
    }
}
