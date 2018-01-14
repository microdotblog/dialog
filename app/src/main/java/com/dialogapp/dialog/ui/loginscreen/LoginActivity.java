package com.dialogapp.dialog.ui.loginscreen;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.AccountResponse;
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

    @OnClick(R.id.button_login)
    public void login() {
        String token = input.getText().toString().trim();
        if (!token.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            loginViewModel.setToken(token);
            loginViewModel.getAccount()
                    .observe(this, accountResponseResource -> {
                        if (accountResponseResource != null) {
                            check(accountResponseResource, token);
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

    private void check(Resource<AccountResponse> accountResponseResource, String token) {
        if (accountResponseResource.status == Status.ERROR) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.login_invalid_token, Toast.LENGTH_SHORT).show();
        } else if (accountResponseResource.status == Status.LOADING) {
            Toast.makeText(this, R.string.login_msg_verification, Toast.LENGTH_SHORT).show();
        } else if (accountResponseResource.status == Status.SUCCESS) {
            progressBar.setVisibility(View.INVISIBLE);
            preferencesHelper.putToken(token);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
