package com.dialogapp.dialog.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.common.AlertDialogFragment;
import com.dialogapp.dialog.ui.common.ListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationActivity extends BaseInjectableActivity implements BaseListFragment.FragmentEventListener,
        AlertDialogFragment.AlertDialogListener {
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
                    .replace(R.id.frame_container, ListFragment.newInstance(ListFragment.CONVERSATION, postId))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoadError(String message) {
        if (!errorBar.isShownOrQueued()) {
            errorBar.setAction("Show error", view -> {
                AlertDialogFragment alertDialog = AlertDialogFragment
                        .newInstance("Connection Error", message, false);
                alertDialog.show(getSupportFragmentManager(), "ErrorAlertDialogFragment");
            });
            errorBar.show();
        }
    }

    @Override
    public void onFinishAlertDialog(boolean userChoice) {

    }
}
