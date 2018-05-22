package com.dialogapp.dialog.ui.common;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.di.Injectable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.dialogapp.dialog.ui.common.ReplyActivity.EXTRA_POSTID;
import static com.dialogapp.dialog.ui.common.ReplyActivity.EXTRA_USERNAME;

public class ReplyFragment extends Fragment implements Injectable {
    @BindView(R.id.reply_input)
    EditText replyContent;

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;
    protected RequestViewModel requestViewModel;

    private Unbinder unbinder;
    private String id;
    private String username;
    private String defaultText;
    private ReplyFragmentEventListener listener;

    public static ReplyFragment newInstance(String id, String username) {
        ReplyFragment listFragment = new ReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_POSTID, id);
        bundle.putString(EXTRA_USERNAME, username);
        listFragment.setArguments(bundle);
        return listFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ReplyFragmentEventListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement ReplyFragmentEventListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(EXTRA_POSTID);
            username = getArguments().getString(EXTRA_USERNAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reply, container, false);
        unbinder = ButterKnife.bind(this, view);

        // allow edit text scrolling
        replyContent.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        defaultText = "@" + username + " ";
        replyContent.setText(defaultText);
        replyContent.setSelection(defaultText.length());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requestViewModel = ViewModelProviders.of(this, viewModelFactory).get(RequestViewModel.class);
        requestViewModel.getResponseReply().observe(this, booleanResource -> {
            if (booleanResource.getContentIfNotHandled() != null) {
                if (booleanResource.peekContent().data) {
                    listener.onSendSuccess();
                } else {
                    listener.onSendFailed();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public boolean hasTextChanged() {
        return !replyContent.getText().toString().equals(defaultText);
    }

    public void sendReply() {
        requestViewModel.sendReply(id, replyContent.getText().toString());
    }

    public void retry() {
        requestViewModel.retryReply();
    }

    public interface ReplyFragmentEventListener {
        void onSendSuccess();

        void onSendFailed();
    }
}
