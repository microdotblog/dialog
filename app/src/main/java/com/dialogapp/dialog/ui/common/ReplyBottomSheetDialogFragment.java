package com.dialogapp.dialog.ui.common;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dialogapp.dialog.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReplyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private static final String EXTRA_POSTID = ReplyBottomSheetDialogFragment.class.getName() + ".EXTRA_POSTID";
    private static final String EXTRA_USERNAME = ReplyBottomSheetDialogFragment.class.getName() + ".EXTRA_USERNAME";

    private Unbinder unbinder;
    private String id;
    private String username;
    private String defaultText;
    private SendButtonClickListener listener;

    @BindView(R.id.text_label_reply)
    TextView label;

    @BindView(R.id.reply_input)
    EditText replyContent;

    @BindView(R.id.button_reply_cancel)
    Button cancel;

    @BindView(R.id.button_reply_send)
    Button send;

    public static ReplyBottomSheetDialogFragment newInstance(String id, String username) {
        ReplyBottomSheetDialogFragment listFragment = new ReplyBottomSheetDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_POSTID, id);
        bundle.putString(EXTRA_USERNAME, username);
        listFragment.setArguments(bundle);
        return listFragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
        });
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
        View view = inflater.inflate(R.layout.bottom_sheet_reply, container, false);
        unbinder = ButterKnife.bind(this, view);

        send.setOnClickListener(view12 -> {
            listener.onSendButtonClicked(replyContent.getText().toString());
            dismiss();
        });

        cancel.setOnClickListener(view1 -> {
            if (!replyContent.getText().toString().equals(defaultText)) {
                AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null, "Discard?");
                dialogFragment.setListener(userChoice -> {
                    if (userChoice)
                        dismiss();
                });
                dialogFragment.show(getFragmentManager(), "ReplyAlertDialogFragment");
            } else {
                dismiss();
            }
        });

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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setSendButtonListener(SendButtonClickListener listener) {
        this.listener = listener;
    }

    public interface SendButtonClickListener {
        void onSendButtonClicked(String text);
    }
}
