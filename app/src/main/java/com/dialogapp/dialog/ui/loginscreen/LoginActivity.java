package com.dialogapp.dialog.ui.loginscreen;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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
import com.dialogapp.dialog.util.Status;
import com.orhanobut.hawk.Hawk;

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
    private String token;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @BindView(R.id.text_login_about)
    TextView about;

    @BindView(R.id.editText_login)
    EditText input;

    @BindView(R.id.progressBar_login)
    ProgressBar progressBar;

    @BindView(R.id.button_login)
    Button loginButton;

    @BindView(R.id.constraint_layout)
    ConstraintLayout rootLayout;

    @OnClick(R.id.button_login)
    public void login() {
        loginButton.setEnabled(false);
        token = input.getText().toString().trim();
        if (!token.isEmpty()) {
            Toast.makeText(this, R.string.login_msg_verification, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);

            loginViewModel.setToken(token);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        about.setMovementMethod(LinkMovementMethod.getInstance());

        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        loginViewModel.verifyToken()
                .observe(this, verifiedAccountResource -> {
                    if (verifiedAccountResource != null) {
                        if (verifiedAccountResource.status == Status.ERROR) {
                            progressBar.setVisibility(View.INVISIBLE);
                            loginButton.setEnabled(true);

                            Snackbar.make(rootLayout, R.string.login_verification_failed, Snackbar.LENGTH_SHORT).show();
                        } else if (verifiedAccountResource.status == Status.SUCCESS && verifiedAccountResource.data != null) {
                            check(verifiedAccountResource.data);
                        }
                    }
                });
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    private void check(VerifiedAccount data) {
        if (data.error == null) {
            progressBar.setVisibility(View.INVISIBLE);

            Hawk.put(getString(R.string.pref_token), token);
            Hawk.put(getString(R.string.pref_username), data.username);
            Hawk.put(getString(R.string.pref_fullname), data.fullName);
            Hawk.put(getString(R.string.pref_avatar_url), data.gravatarUrl);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_TOKEN, token);
            intent.putExtra(EXTRA_USERNAME, data.username);
            intent.putExtra(EXTRA_FULLNAME, data.fullName);
            intent.putExtra(EXTRA_AVATARURL, data.gravatarUrl);
            startActivity(intent);
            finish();
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            loginButton.setEnabled(true);

            Snackbar.make(rootLayout, R.string.login_invalid_token, Snackbar.LENGTH_LONG).show();
        }
    }
}
