package com.dialogapp.dialog.ui.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.api.ServiceInterceptor;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.favorites.FavoritesActivity;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dialogapp.dialog.LauncherActivity.EXTRA_AVATARURL;
import static com.dialogapp.dialog.LauncherActivity.EXTRA_FULLNAME;
import static com.dialogapp.dialog.LauncherActivity.EXTRA_TOKEN;
import static com.dialogapp.dialog.LauncherActivity.EXTRA_USERNAME;

public class MainActivity extends BaseInjectableActivity implements BaseListFragment.FragmentEventListener,
        NavigationView.OnNavigationItemSelectedListener {

    private Snackbar errorBar;

    @Inject
    ServiceInterceptor serviceInterceptor;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.coord_layout_main)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);

        errorBar = Snackbar.make(coordinatorLayout, R.string.connection_error, Snackbar.LENGTH_LONG);

        Intent intent = getIntent();
        String saved_token = intent.getStringExtra(EXTRA_TOKEN);
        String saved_username = intent.getStringExtra(EXTRA_USERNAME);
        String saved_fullname = intent.getStringExtra(EXTRA_FULLNAME);
        String saved_avatarUrl = intent.getStringExtra(EXTRA_AVATARURL);

        serviceInterceptor.setAuthToken(saved_token);

        navigationView.setNavigationItemSelectedListener(this);

        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.image_profile);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.text_username);
        TextView fullname = navigationView.getHeaderView(0).findViewById(R.id.text_fullname);

        Glide.with(this)
                .load(saved_avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
        username.setText(saved_username);
        fullname.setText(saved_fullname);

        setupViewpager();
    }

    @Override
    public void onLoadError(String message) {
        errorBar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        switch (id) {
            case R.id.menu_item_profile:
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USERNAME, getIntent().getStringExtra(EXTRA_USERNAME));
                break;
            case R.id.menu_item_fav:
                intent = new Intent(this, FavoritesActivity.class);
                break;
            case R.id.menu_item_settings:
                return false;
        }

        startActivity(intent);

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void setupViewpager() {
        ViewPager viewPager = findViewById(R.id.viewpager_main);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.tab_ic_timeline_white_24px);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_ic_mentions_white_24px);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_ic_discover_white_24px);
    }
}
