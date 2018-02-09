package com.dialogapp.dialog.ui.mainscreen.common;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dialogapp.dialog.R;
import com.dialogapp.dialog.model.Item;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostViewHolder extends BaseViewHolder<Item> {
    @BindView(R.id.image_thumbnail)
    ImageView thumbnail;

    @BindView(R.id.text_username)
    TextView username;

    @BindView(R.id.text_content)
    TextView content;

    @BindView(R.id.image_content_pic)
    ImageView content_pic;

    @BindView(R.id.text_time)
    TextView time;

    public PostViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBind(Item item, int position) {
        Glide.with(this.itemView)
                .load(item.author.avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(thumbnail);

        username.setText(item.author.microblog.username);
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setText(Html.fromHtml(item.contentHtml));
        time.setText(item.microblog.dateRelative);
    }
}
