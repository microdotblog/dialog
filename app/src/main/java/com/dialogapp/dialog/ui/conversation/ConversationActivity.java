package com.dialogapp.dialog.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationActivity extends BaseInjectableActivity implements BaseListFragment.FragmentEventListener {
    public static final String EXTRA_POST_ID = ConversationActivity.class.getName() + ".EXTRA_POST_ID";
    private Snackbar errorBar;

    @BindView(R.id.toolbar_container)
    Toolbar toolbar;

    @BindView(R.id.coord_layout_container)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);

        errorBar = Snackbar.make(coordinatorLayout, R.string.connection_error, Snackbar.LENGTH_LONG);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String postId = intent.getStringExtra(EXTRA_POST_ID);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, ConversationFragment.newInstance(postId))
                    .commit();
        }
    }

    @Override
    public void onLoadError(String message) {
        if (!errorBar.isShownOrQueued()) {
            errorBar.setAction("Show error", view -> {
                new MaterialDialog.Builder(this)
                        .title("Connection Error")
                        .content(message)
                        .positiveText(R.string.dialog_dismiss)
                        .show();
            });
            errorBar.show();
        }
    }
}
