package com.dialogapp.dialog.ui.loginscreen;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.ui.mainscreen.MainActivity;
import com.dialogapp.dialog.util.PreferencesHelper;
import com.dialogapp.dialog.util.Resource;
import com.dialogapp.dialog.util.Status;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_AVATARURL;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_FULLNAME;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_TOKEN;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_USERNAME;

public class LoginActivity extends AppCompatActivity implements HasActivityInjector {

    private LoginViewModel loginViewModel;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PreferencesHelper preferencesHelper;

    @BindView(R.id.text_login_about)
    TextView about;

    @BindView(R.id.editText_login)
    EditText input;

    @BindView(R.id.progressBar_login)
    ProgressBar progressBar;

    @BindView(R.id.button_login)
    Button loginButton;

    @OnClick(R.id.button_login)
    public void login() {
        loginButton.setEnabled(false);
        String token = input.getText().toString().trim();
        if (!token.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            loginViewModel.setToken(token);
            loginViewModel.verifyToken()
                    .observe(this, verifiedAccountResource -> {
                        if (verifiedAccountResource != null) {
                            check(verifiedAccountResource, token);
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        about.setMovementMethod(LinkMovementMethod.getInstance());

        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    private void check(Resource<VerifiedAccount> verifiedAccountResource, String token) {
        if (verifiedAccountResource.status == Status.ERROR) {
            progressBar.setVisibility(View.INVISIBLE);
            loginButton.setEnabled(true);

            Toast.makeText(this, R.string.login_verification_failed, Toast.LENGTH_SHORT).show();
        } else if (verifiedAccountResource.status == Status.LOADING) {
            Toast.makeText(this, R.string.login_msg_verification, Toast.LENGTH_SHORT).show();
        } else if (verifiedAccountResource.status == Status.SUCCESS) {
            if (verifiedAccountResource.data != null) {
                if (verifiedAccountResource.data.error == null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    preferencesHelper.putToken(token);
                    preferencesHelper.putUsername(verifiedAccountResource.data.username);
                    preferencesHelper.putFullname(verifiedAccountResource.data.fullName);
                    preferencesHelper.putAvatarUrl(verifiedAccountResource.data.gravatarUrl);

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(EXTRA_TOKEN, token);
                    intent.putExtra(EXTRA_USERNAME, verifiedAccountResource.data.username);
                    intent.putExtra(EXTRA_FULLNAME, verifiedAccountResource.data.fullName);
                    intent.putExtra(EXTRA_AVATARURL, verifiedAccountResource.data.gravatarUrl);
                    startActivity(intent);
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    loginButton.setEnabled(true);

                    Toast.makeText(this, R.string.login_invalid_token, Toast.LENGTH_SHORT).show();
                }
            } else {
                // resource data is somehow null
                progressBar.setVisibility(View.INVISIBLE);
                loginButton.setEnabled(true);

                Toast.makeText(this, R.string.login_verification_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
