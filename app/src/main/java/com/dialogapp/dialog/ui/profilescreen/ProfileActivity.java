package com.dialogapp.dialog.ui.profilescreen;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.TooltipCompat;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.UserInfo;
import com.dialogapp.dialog.ui.base.BaseInjectableActivity;
import com.dialogapp.dialog.ui.base.BaseListFragment;
import com.dialogapp.dialog.ui.common.AlertDialogFragment;
import com.dialogapp.dialog.ui.common.RequestViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseInjectableActivity implements BaseListFragment.FragmentEventListener,
        AlertDialogFragment.AlertDialogListener, ProfileFragment.UserDataEventListener {
    public static final String EXTRA_USERNAME = ProfileActivity.class.getName() + ".EXTRA_USERNAME";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ProfileFragmentPagerAdapter adapter;
    private RequestViewModel viewModel;

    @BindView(R.id.toolbar_profile)
    Toolbar toolbar;

    @BindView(R.id.button_follow)
    ImageButton followButton;

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
        initFavButton();

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RequestViewModel.class);
        viewModel.getResponseFollow().observe(this, resourceEvent -> {
            if (resourceEvent.getContentIfNotHandled() != null) {
                if (resourceEvent.peekContent().data) {
                    Toast.makeText(this, "Request successful",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Request failed", Toast.LENGTH_SHORT).show();
                    followButton.setEnabled(true);
                }
            }
        });
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
    public void onUserDataLoadSuccess(UserInfo userInfo) {
        if (userInfo != null) {
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

            followButton.setVisibility(View.VISIBLE);
            followButton.setEnabled(true);
            if (userInfo.microblog_is_following) {
                TooltipCompat.setTooltipText(followButton, "Unfollow");
                followButton.setImageResource(R.drawable.ic_person_minus_white_24dp);
                followButton.setTag(true);
            } else {
                TooltipCompat.setTooltipText(followButton, "Follow");
                followButton.setImageResource(R.drawable.ic_person_add_white_24dp);
                followButton.setTag(false);
            }
        }
    }

    @Override
    public void onUserDataLoading() {
        followButton.setEnabled(false);
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

    private void initFavButton() {
        followButton.setEnabled(false);
        followButton.setOnClickListener(view -> {
            Toast.makeText(this, "Requesting...", Toast.LENGTH_SHORT).show();
            followButton.setEnabled(false);
            viewModel.setFollowState(getIntent().getStringExtra(EXTRA_USERNAME), !((boolean) followButton.getTag()));
        });
    }
}
