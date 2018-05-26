package com.dialogapp.dialog.ui.common;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.TooltipCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReplyActivity extends BaseInjectableActivity implements ReplyFragment.ReplyFragmentEventListener {
    public static final String EXTRA_POSTID = ReplyActivity.class.getName() + ".EXTRA_POSTID";
    public static final String EXTRA_USERNAME = ReplyActivity.class.getName() + ".EXTRA_USERNAME";

    @BindView(R.id.toolbar_reply)
    Toolbar toolbar;

    @BindView(R.id.container_reply)
    CoordinatorLayout containerView;

    @BindView(R.id.bottom_sheet_reply_content)
    CoordinatorLayout contentView;

    @BindView(R.id.appbar_reply)
    AppBarLayout appBarLayout;

    @BindView(R.id.button_reply_send)
    ImageButton replyButton;

    @OnClick(R.id.button_reply_send)
    public void sendReply() {
        requestInProgress = true;
        replyButton.setEnabled(false);
        ReplyFragment fragment = (ReplyFragment) getSupportFragmentManager().findFragmentByTag("ReplyFragment");
        if (fragment != null) {
            Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show();
            if (failed)
                fragment.retry();
            else
                fragment.sendReply();
            failed = false;
        }
    }

    private boolean failed;
    private boolean requestInProgress;
    private BottomSheetBehavior<CoordinatorLayout> bottomSheet;
    private int lastState = BottomSheetBehavior.STATE_COLLAPSED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String postId = getIntent().getStringExtra(EXTRA_POSTID);
        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (postId == null || username == null)
            finish();

        setStatusBarDim(true);
        setContentView(R.layout.activity_reply);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TooltipCompat.setTooltipText(findViewById(R.id.button_reply_send), "Send");

        if (isNight()) {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.grey850));
        } else {
            appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.grey50));
        }

        findViewById(R.id.touch_outside).setOnClickListener(v -> {
            shouldFinish();
        });

        bottomSheet = BottomSheetBehavior.from(contentView);
        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        shouldFinish();
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        setStatusBarDim(false);
                        lastState = newState;
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        setStatusBarDim(true);
                        lastState = newState;
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // no op
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.reply_fragment_container, ReplyFragment.newInstance(postId, username), "ReplyFragment")
                    .commit();
        } else {
            failed = savedInstanceState.getBoolean("RequestStatus", false);
            requestInProgress = savedInstanceState.getBoolean("RequestInProgress", false);
            lastState = savedInstanceState.getInt("BottomSheetState", BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheet.setState(lastState);
            replyButton.setEnabled(!requestInProgress);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("BottomSheetState", lastState);
        outState.putBoolean("RequestStatus", failed);
        outState.putBoolean("RequestInProgress", requestInProgress);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                shouldFinish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSendSuccess() {
        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSendFailed() {
        failed = true;
        requestInProgress = false;
        replyButton.setEnabled(true);
        Snackbar snackbar = Snackbar.make(containerView, "Could not send reply", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.retry_reply, view -> {
            sendReply();
            snackbar.dismiss();
        });
        snackbar.show();
    }

    private void shouldFinish() {
        if (requestInProgress) {
            if (bottomSheet.getState() == BottomSheetBehavior.STATE_HIDDEN)
                bottomSheet.setState(lastState);
            return;
        }
        ReplyFragment fragment = (ReplyFragment) getSupportFragmentManager().findFragmentByTag("ReplyFragment");
        if (fragment != null) {
            if (fragment.hasTextChanged()) {
                new MaterialDialog.Builder(this)
                        .content("Discard?")
                        .positiveText(R.string.dialog_ok)
                        .negativeText(R.string.dialog_cancel)
                        .onPositive((dialog1, which) -> {
                            dialog1.dismiss();
                            finish();
                        })
                        .onNegative((dialog, which) -> {
                            bottomSheet.setState(lastState);
                        })
                        .canceledOnTouchOutside(false)
                        .show();
            } else {
                finish();
            }
        }
    }

    private void setStatusBarDim(boolean dim) {
        getWindow().setStatusBarColor(dim ? Color.TRANSPARENT :
                ContextCompat.getColor(this, getThemedResId(android.R.attr.statusBarColor)));
    }

    private int getThemedResId(@AttrRes int attr) {
        TypedArray a = getTheme().obtainStyledAttributes(new int[]{attr});
        int resId = a.getResourceId(0, 0);
        a.recycle();
        return resId;
    }

    private boolean isNight() {
        return (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
