package com.dialogapp.dialog.ui;

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
import com.dialogapp.dialog.repository.AccountRepository;
import com.dialogapp.dialog.ui.mainscreen.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

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
            if (!AccountRepository.isValid(token)) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.login_invalid_token, Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        about.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
