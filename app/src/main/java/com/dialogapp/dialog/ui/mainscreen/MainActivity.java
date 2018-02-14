package com.dialogapp.dialog.ui.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.api.ServiceInterceptor;
import com.dialogapp.dialog.ui.common.BaseListFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_AVATARURL;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_FULLNAME;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_TOKEN;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_USERNAME;

public class MainActivity extends AppCompatActivity implements BaseListFragment.FragmentEventListener,
        HasSupportFragmentInjector {

    private Snackbar errorBar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ServiceInterceptor serviceInterceptor;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.coord_layout_main)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        errorBar = Snackbar.make(coordinatorLayout, R.string.connection_error, Snackbar.LENGTH_LONG);

        Intent intent = getIntent();
        String saved_token = intent.getStringExtra(EXTRA_TOKEN);
        String saved_username = intent.getStringExtra(EXTRA_USERNAME);
        String saved_fullname = intent.getStringExtra(EXTRA_FULLNAME);
        String saved_avatarUrl = intent.getStringExtra(EXTRA_AVATARURL);

        serviceInterceptor.setAuthToken(saved_token);

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
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public void onLoadError(String message) {
        errorBar.show();
    }

    private void setupViewpager() {
        ViewPager viewPager = findViewById(R.id.viewpager_main);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }
}
