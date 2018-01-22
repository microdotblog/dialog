package com.dialogapp.dialog.ui.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_AVATARURL;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_FULLNAME;
import static com.dialogapp.dialog.ui.LauncherActivity.EXTRA_USERNAME;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String saved_username = intent.getStringExtra(EXTRA_USERNAME);
        String saved_fullname = intent.getStringExtra(EXTRA_FULLNAME);
        String saved_avatarUrl = intent.getStringExtra(EXTRA_AVATARURL);

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
    }
}
