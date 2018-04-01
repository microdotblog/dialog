package com.dialogapp.dialog.ui.favorites;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.common.ListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends BaseInjectableActivity implements BaseListFragment.FragmentEventListener {
    private Snackbar errorBar;

    @BindView(R.id.coord_layout_common)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        ButterKnife.bind(this);

        errorBar = Snackbar.make(coordinatorLayout, R.string.connection_error, Snackbar.LENGTH_LONG);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_common, ListFragment.newInstance(ListFragment.FAVORITES))
                .commit();
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
        errorBar.show();
    }
}
