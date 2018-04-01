package com.dialogapp.dialog.ui.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.mainscreen.MainActivity;

import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_AVATARURL;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_FULLNAME;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_TOKEN;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_USERNAME;

public class LoginActivity extends BaseInjectableActivity implements LoginFragment.LoginFragmentEventListener {
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        coordinatorLayout = findViewById(R.id.coord_layout_common);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_common, new LoginFragment())
                .commit();
    }

    @Override
    public void showVerifyingMsg() {
        Toast.makeText(this, R.string.login_msg_verification, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVerificationFailed() {
        Snackbar.make(coordinatorLayout, R.string.login_verification_failed, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onInvalidToken() {
        Snackbar.make(coordinatorLayout, R.string.login_invalid_token, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onVerificationComplete(String token, String username, String fullName, String gravatarUrl) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_TOKEN, token);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_FULLNAME, fullName);
        intent.putExtra(EXTRA_AVATARURL, gravatarUrl);
        startActivity(intent);
        finish();
    }
}
