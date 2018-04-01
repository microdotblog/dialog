package com.dialogapp.dialog.ui.profilescreen;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.ui.base.BaseListFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class ProfileActivity extends AppCompatActivity implements BaseListFragment.FragmentEventListener<MicroBlogResponse>,
        HasSupportFragmentInjector {
    public static final String EXTRA_USERNAME = ProfileActivity.class.getName() + ".EXTRA_USERNAME";

    private ProfileFragmentPagerAdapter adapter;
    private Snackbar errorBar;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @BindView(R.id.toolbar_profile)
    Toolbar toolbar;

    @BindView(R.id.coord_layout_profile)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.view_pager_profile)
    ViewPager viewPager;

    @BindView(R.id.tab_layout_profile)
    TabLayout tabLayout;

    @BindView(R.id.image_profile)
    ImageView avatar;

    @BindView(R.id.text_profile_fullname)
    TextView fullname;

    @BindView(R.id.text_profile_website)
    TextView website;

    @BindView(R.id.text_profile_about)
    TextView about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        errorBar = Snackbar.make(coordinatorLayout, R.string.connection_error, Snackbar.LENGTH_LONG);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(getIntent().getStringExtra(EXTRA_USERNAME));

        setupViewpager();
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
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
    @SuppressWarnings("ConstantConditions")
    public void onLoadSuccess(MicroBlogResponse data) {
        Glide.with(this).load(data.author.avatar)
                .apply(new RequestOptions().circleCrop())
                .into(avatar);
        fullname.setText(data.author.name);
        if (!data.author.url.isEmpty()) {
            website.setVisibility(View.VISIBLE);
            website.setText(data.author.url);
        }
        if (!data.microblog.bio.isEmpty()) {
            about.setVisibility(View.VISIBLE);
            about.setText(data.microblog.bio);
        }
    }

    @Override
    public void onLoadError(String message) {
        errorBar.show();
    }

    private void setupViewpager() {
        adapter = new ProfileFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ProfileFragment.newInstance(getIntent().getStringExtra(EXTRA_USERNAME)));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
