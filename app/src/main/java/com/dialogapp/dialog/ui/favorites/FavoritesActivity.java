package com.dialogapp.dialog.ui.favorites;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends BaseListActivity {
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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new FavoritesFragment())
                    .commit();
        }
    }
}
