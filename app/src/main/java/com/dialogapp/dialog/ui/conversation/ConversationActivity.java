package com.dialogapp.dialog.ui.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationActivity extends BaseListActivity {
    public static final String EXTRA_POST_ID = ConversationActivity.class.getName() + ".EXTRA_POST_ID";

    @BindView(R.id.toolbar_container)
    Toolbar toolbar;

    @BindView(R.id.coord_layout_container)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);
        setErrorBar(coordinatorLayout);

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
}
