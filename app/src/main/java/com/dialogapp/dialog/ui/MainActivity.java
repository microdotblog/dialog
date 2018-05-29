package com.dialogapp.dialog.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.base.BaseListActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.common.ChangelogDialog;
import com.dialogapp.dialog.ui.loginscreen.LoginActivity;
import com.dialogapp.dialog.ui.mainscreen.DiscoverFragment;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.ui.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseListActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.coord_layout_main)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.spinner_discover)
    Spinner spinnerDiscover;

    private static final int TIME_INTERVAL = 2000;
    private String[] category = {"Recent", "Books", "Music", "Podcasts"};
    private ViewPager viewPager;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getSavedFullname());
        setErrorBar(coordinatorLayout);

        setSpinnerDiscover();
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
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USERNAME, getSavedUsername());
                startActivity(intent);
                return true;
            case R.id.main_options_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(ProfileActivity.EXTRA_USERNAME, getSavedUsername());
                startActivity(intent);
                return true;
            case R.id.main_options_logout:
                showLogoutDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
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
                ((DiscoverFragment) getSupportFragmentManager()
                        .findFragmentByTag(getViewpagerFragmentByTag(viewPager.getId(), 2)))
                        .setTopic(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                BaseListFragment fragment = (BaseListFragment) getSupportFragmentManager()
                        .findFragmentByTag(getViewpagerFragmentByTag(viewPager.getId(), tab.getPosition()));
                if (fragment != null) {
                    fragment.scrollListToTop();
                }
            }
        });
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
                        getSupportActionBar().setDisplayShowTitleEnabled(true);
                        break;
                    case 1:
                        tabLayout.getTabAt(0).getIcon().setAlpha(178);
                        tabLayout.getTabAt(1).getIcon().setAlpha(255);
                        tabLayout.getTabAt(2).getIcon().setAlpha(178);
                        spinnerDiscover.setVisibility(View.GONE);
                        getSupportActionBar().setDisplayShowTitleEnabled(true);
                        break;
                    case 2:
                        tabLayout.getTabAt(0).getIcon().setAlpha(178);
                        tabLayout.getTabAt(1).getIcon().setAlpha(178);
                        tabLayout.getTabAt(2).getIcon().setAlpha(255);
                        spinnerDiscover.setVisibility(View.VISIBLE);
                        getSupportActionBar().setDisplayShowTitleEnabled(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
