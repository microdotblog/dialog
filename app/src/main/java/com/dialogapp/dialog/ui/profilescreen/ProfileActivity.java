package com.dialogapp.dialog.ui.profilescreen;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.UserInfo;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.common.AlertDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseInjectableActivity implements BaseListFragment.FragmentEventListener,
        AlertDialogFragment.AlertDialogListener {
    public static final String EXTRA_USERNAME = ProfileActivity.class.getName() + ".EXTRA_USERNAME";

    private ProfileFragmentPagerAdapter adapter;

    @BindView(R.id.toolbar_profile)
    Toolbar toolbar;

    @BindView(R.id.coord_layout_profile)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.view_pager_profile)
    ViewPager viewPager;

    @BindView(R.id.tab_layout_profile)
    TabLayout tabLayout;

    @BindView(R.id.image_avatar)
    CircleImageView avatar;

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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitle(getIntent().getStringExtra(EXTRA_USERNAME));

        setupViewpager();
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
    public void onLoadSuccess(Object data) {
        UserInfo userInfo = (UserInfo) data;

        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorCardBackground, outValue, true);
        Glide.with(this)
                .load(userInfo.author_author_avatar_url)
                .apply(RequestOptions.placeholderOf(outValue.resourceId))
                .apply(RequestOptions.noAnimation())
                .into(avatar);
        fullname.setText(userInfo.author_author_name);
        if (!userInfo.author_author_url.isEmpty())
            website.setText(userInfo.author_author_url);
        else
            website.setVisibility(View.GONE);
        if (!userInfo.microblog_bio.isEmpty())
            about.setText(userInfo.microblog_bio);
        else
            about.setVisibility(View.GONE);
    }

    @Override
    public void onLoadError(String message) {
        Snackbar errorBar = Snackbar.make(coordinatorLayout, R.string.connection_error, Snackbar.LENGTH_LONG);
        errorBar.setAction("Show error", view -> {
            AlertDialogFragment alertDialog = AlertDialogFragment
                    .newInstance("Connection Error", message, false);
            alertDialog.show(getSupportFragmentManager(), "ErrorAlertDialogFragment");
        });
        errorBar.show();
    }

    private void setupViewpager() {
        adapter = new ProfileFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(ProfileFragment.newInstance(getIntent().getStringExtra(EXTRA_USERNAME)));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFinishAlertDialog(boolean userChoice) {

    }
}
