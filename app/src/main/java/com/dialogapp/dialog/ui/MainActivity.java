package com.dialogapp.dialog.ui;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseListActivity;
import com.dialogapp.dialog.ui.common.ChangelogDialog;
import com.dialogapp.dialog.ui.favorites.FavoritesActivity;
import com.dialogapp.dialog.ui.loginscreen.LoginActivity;
import com.dialogapp.dialog.ui.mainscreen.DiscoverFragment;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.ui.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseListActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String[] category = {"Recent", "Books", "Music", "Podcasts"};

    private ViewPager viewPager;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.coord_layout_main)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.spinner_discover)
    Spinner spinnerDiscover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);
        setErrorBar(coordinatorLayout);

        navigationView.setNavigationItemSelectedListener(this);
        setSpinnerDiscover();

        CircleImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.image_profile);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.text_username);
        TextView fullname = navigationView.getHeaderView(0).findViewById(R.id.text_fullname);
        ImageView logout = navigationView.getHeaderView(0).findViewById(R.id.image_logout);

        Glide.with(this)
                .load(getSavedAvatarUrl())
                .apply(RequestOptions.noAnimation())
                .into(imageView);
        username.setText(getSavedUsername());
        fullname.setText(getSavedFullname());
        logout.setOnClickListener(view -> {
            showLogoutDialog();
        });

        setupViewpager();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!sharedPrefUtil.isReleaseNotesSeen(this)) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Dialog has been updated!",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("What's new",
                    v -> {
                        snackbar.dismiss();
                        sharedPrefUtil.setReleaseNotesSeen(this);
                        ChangelogDialog.create((getResources().getConfiguration().uiMode
                                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                                .show(getSupportFragmentManager(), "changelogdialog");
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                intent.putExtra(ProfileActivity.EXTRA_USERNAME, getSavedUsername());
                break;
            case R.id.menu_item_fav:
                intent = new Intent(this, FavoritesActivity.class);
                break;
            case R.id.menu_item_settings:
                intent = new Intent(this, SettingsActivity.class);
        }

        startActivity(intent);

        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void startLoginActivity() {
        sharedPrefUtil.remove(getString(R.string.pref_token));
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void showLogoutDialog() {
        new MaterialDialog.Builder(this)
                .content("Do you want to log out?")
                .positiveText(R.string.dialog_ok)
                .negativeText(R.string.dialog_cancel)
                .onPositive((dialog, which) -> {
                    startLoginActivity();
                })
                .show();
    }

    private void setSpinnerDiscover() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiscover.setAdapter(adapter);
        spinnerDiscover.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ((DiscoverFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag(viewPager.getId())))
                        .setTopic(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private String getFragmentTag(int viewPagerId) {
        return "android:switcher:" + viewPagerId + ":" + 2;
    }

    private void setupViewpager() {
        viewPager = findViewById(R.id.viewpager_main);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.tab_ic_view_stream_white_24px);
        tabLayout.getTabAt(1).setIcon(R.drawable.tab_ic_chat_white_24px);
        tabLayout.getTabAt(2).setIcon(R.drawable.tab_ic_discover_white_24px);

        tabLayout.getTabAt(1).getIcon().setAlpha(178);
        tabLayout.getTabAt(2).getIcon().setAlpha(178);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabLayout.getTabAt(0).getIcon().setAlpha(255);
                        tabLayout.getTabAt(1).getIcon().setAlpha(178);
                        tabLayout.getTabAt(2).getIcon().setAlpha(178);
                        spinnerDiscover.setVisibility(View.GONE);
                        break;
                    case 1:
                        tabLayout.getTabAt(0).getIcon().setAlpha(178);
                        tabLayout.getTabAt(1).getIcon().setAlpha(255);
                        tabLayout.getTabAt(2).getIcon().setAlpha(178);
                        spinnerDiscover.setVisibility(View.GONE);
                        break;
                    case 2:
                        tabLayout.getTabAt(0).getIcon().setAlpha(178);
                        tabLayout.getTabAt(1).getIcon().setAlpha(178);
                        tabLayout.getTabAt(2).getIcon().setAlpha(255);
                        spinnerDiscover.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
