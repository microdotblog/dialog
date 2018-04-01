package com.dialogapp.dialog.ui.loginscreen;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.dialogapp.dialog.util.Status;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginFragment extends Fragment implements Injectable {

    private LoginViewModel loginViewModel;
    private Unbinder unbinder;
    private String token;
    private LoginFragmentEventListener listener;

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

    @OnClick(R.id.button_login)
    public void login() {
        token = input.getText().toString().trim();
        if (!token.isEmpty()) {
            loginButton.setEnabled(false);
            listener.showVerifyingMsg();
            progressBar.setVisibility(View.VISIBLE);

            loginViewModel.setToken(token);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (LoginFragmentEventListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement LoginFragmentEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        about.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        loginViewModel.verifyToken()
                .observe(this, verifiedAccountResource -> {
                    if (verifiedAccountResource != null) {
                        if (verifiedAccountResource.status == Status.ERROR) {
                            progressBar.setVisibility(View.INVISIBLE);
                            loginButton.setEnabled(true);

                            listener.onVerificationFailed();
                        } else if (verifiedAccountResource.status == Status.SUCCESS && verifiedAccountResource.data != null) {
                            check(verifiedAccountResource.data);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void check(VerifiedAccount data) {
        if (data.error == null) {
            progressBar.setVisibility(View.INVISIBLE);

            Hawk.put(getString(R.string.pref_token), token);
            Hawk.put(getString(R.string.pref_username), data.username);
            Hawk.put(getString(R.string.pref_fullname), data.fullName);
            Hawk.put(getString(R.string.pref_avatar_url), data.gravatarUrl);

            listener.onVerificationComplete(token, data.username, data.fullName, data.gravatarUrl);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            loginButton.setEnabled(true);

            listener.onInvalidToken();
        }
    }

    public interface LoginFragmentEventListener {
        void showVerifyingMsg();

        void onVerificationFailed();

        void onInvalidToken();

        void onVerificationComplete(String token, String username, String fullName, String gravatarUrl);
    }
}
