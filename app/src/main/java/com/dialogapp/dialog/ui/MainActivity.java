package com.dialogapp.dialog.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.api.ServiceInterceptor;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.common.AlertDialogFragment;
import com.dialogapp.dialog.ui.favorites.FavoritesActivity;
import com.dialogapp.dialog.ui.loginscreen.LoginActivity;
import com.dialogapp.dialog.ui.loginscreen.LoginViewModel;
import com.dialogapp.dialog.ui.profilescreen.ProfileActivity;
import com.dialogapp.dialog.ui.settings.SettingsActivity;
import com.dialogapp.dialog.util.Status;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseInjectableActivity implements BaseListFragment.FragmentEventListener,
        NavigationView.OnNavigationItemSelectedListener, AlertDialogFragment.AlertDialogListener {
    public static final String EXTRA_USERNAME = MainActivity.class.getName() + ".EXTRA_USERNAME";
    public static final String EXTRA_FULLNAME = MainActivity.class.getName() + ".EXTRA_FULLNAME";
    public static final String EXTRA_AVATARURL = MainActivity.class.getName() + ".EXTRA_AVATARURL";
    public static final String EXTRA_TOKEN = MainActivity.class.getName() + ".EXTRA_TOKEN";

    private Snackbar errorBar;
    private boolean errorHasBeenShown;

    @Inject
    ServiceInterceptor serviceInterceptor;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PostsDao postsDao;

    @Inject
    AppExecutors appExecutors;

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

        LoginViewModel loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        if (savedInstanceState == null) {
            loginViewModel.setToken(saved_token);
        }
        loginViewModel.verifyToken().observe(this, verifiedAccountResource -> {
            if (verifiedAccountResource != null) {
                if (verifiedAccountResource.status == Status.ERROR) {
                    Toast.makeText(this, R.string.login_verification_failed, Toast.LENGTH_SHORT).show();
                } else if (verifiedAccountResource.status == Status.SUCCESS && verifiedAccountResource.data != null) {
                    if (verifiedAccountResource.data.error != null) {
                        Toast.makeText(this, R.string.login_invalid_token, Toast.LENGTH_LONG).show();
                        startLoginActivity();
                    }
                }
            }
        });

        serviceInterceptor.setAuthToken(saved_token);

        navigationView.setNavigationItemSelectedListener(this);

        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.image_profile);
        TextView username = navigationView.getHeaderView(0).findViewById(R.id.text_username);
        TextView fullname = navigationView.getHeaderView(0).findViewById(R.id.text_fullname);
        ImageView logout = navigationView.getHeaderView(0).findViewById(R.id.image_logout);

        Glide.with(this)
                .load(saved_avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
        username.setText(saved_username);
        fullname.setText(saved_fullname);
        logout.setOnClickListener(view -> {
            showLogoutDialog();
        });

        setupViewpager();
    }

    @Override
    public void onLoadError(String message) {
        if (!errorHasBeenShown && !errorBar.isShownOrQueued()) {
            errorBar.setAction("Show error", view -> {
                AlertDialogFragment alertDialog = AlertDialogFragment
                        .newInstance("Connection Error", message, false);
                alertDialog.show(getSupportFragmentManager(), "ErrorAlertDialogFragment");
            });
            errorBar.show();
            errorHasBeenShown = true;
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
                intent.putExtra(ProfileActivity.EXTRA_USERNAME, getIntent().getStringExtra(EXTRA_USERNAME));
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

    @Override
    public void onFinishAlertDialog(boolean userChoice) {
        if (userChoice)
            startLoginActivity();
    }

    private void startLoginActivity() {
        Hawk.delete(getString(R.string.pref_token));
        appExecutors.diskIO().execute(() -> postsDao.dropTable());
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void showLogoutDialog() {
        AlertDialogFragment alertDialog = AlertDialogFragment.newInstance(null,
                "Do you want to log out?", true);
        alertDialog.show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void setupViewpager() {
        ViewPager viewPager = findViewById(R.id.viewpager_main);
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
                        break;
                    case 1:
                        tabLayout.getTabAt(0).getIcon().setAlpha(178);
                        tabLayout.getTabAt(1).getIcon().setAlpha(255);
                        tabLayout.getTabAt(2).getIcon().setAlpha(178);
                        break;
                    case 2:
                        tabLayout.getTabAt(0).getIcon().setAlpha(178);
                        tabLayout.getTabAt(1).getIcon().setAlpha(178);
                        tabLayout.getTabAt(2).getIcon().setAlpha(255);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
