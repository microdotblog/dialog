package com.dialogapp.dialog.ui.favorites;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends BaseInjectableActivity implements BaseListFragment.FragmentEventListener {
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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new FavoritesFragment())
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
