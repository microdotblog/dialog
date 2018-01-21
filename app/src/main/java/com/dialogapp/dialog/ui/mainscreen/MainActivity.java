package com.dialogapp.dialog.ui.mainscreen;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.util.PreferencesHelper;
import com.dialogapp.dialog.util.Resource;
import com.dialogapp.dialog.util.Status;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class MainActivity extends AppCompatActivity implements HasActivityInjector {

    private MainViewModel mainViewModel;
    private ImageView imageView;
    private TextView username;
    private TextView fullname;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PreferencesHelper preferencesHelper;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        imageView = findViewById(R.id.image_profile);
        username = findViewById(R.id.text_username);
        fullname = findViewById(R.id.text_fullname);

        String username = preferencesHelper.fetchUsername(getString(R.string.pref_username));
        String token = preferencesHelper.fetchUsername(getString(R.string.pref_token));

        // close drawer until account data has been loaded
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        mainViewModel.setAccountInfo(token, username);
        mainViewModel.getAccountData()
                .observe(this, accountResponseResource -> {
                    if (accountResponseResource != null) {
                        check(accountResponseResource);
                    }
                });
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    private void check(Resource<AccountResponse> accountResponseResource) {
        if (accountResponseResource.status == Status.SUCCESS) {
            if (accountResponseResource.data != null) {
                Glide.with(this)
                        .load(preferencesHelper.fetchAvatarUrl(getString(R.string.pref_avatar_url)))
                        .apply(RequestOptions.circleCropTransform())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
                username.setText(accountResponseResource.data.getUsername());
                fullname.setText(accountResponseResource.data.getFullName());

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            } else {
                // account data is somehow null
                mainViewModel.retry();
            }
        } else if (accountResponseResource.status == Status.ERROR) {
            mainViewModel.retry();
        }
    }
}
