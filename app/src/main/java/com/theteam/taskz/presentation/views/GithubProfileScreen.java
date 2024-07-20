package com.theteam.taskz.presentation.views;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.GithubAccount;
import com.theteam.taskz.data.models.UserData;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GithubProfileScreen extends AppCompatActivity {

    private GithubAccount account;

    private CircleImageView profile_icon;
    private TextView name_text,username_text,followers_text,following_text,public_repos_text;
    private ImageView link_icon,open_icon,follow_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra("github_account")){
            account = GithubAccount.fromJson(new Gson().fromJson(getIntent().getStringExtra("github_account"), new TypeToken<HashMap<String,Object>>(){}.getType()));
        }
        else{
            account = UserData.githubAccount(getApplicationContext());
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.github_profile_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profile_icon = findViewById(R.id.profile_icon);
        name_text = (TextView) findViewById(R.id.name_text);
        username_text = (TextView) findViewById(R.id.username_text);
        followers_text = (TextView) findViewById(R.id.followers);
        following_text = (TextView) findViewById(R.id.following);
        public_repos_text = (TextView) findViewById(R.id.repos);
        name_text.setText(account.name);
        username_text.setText("@" + account.username);
        followers_text.setText(String.valueOf(account.followers));
        following_text.setText(String.valueOf(account.following));
        public_repos_text.setText(String.valueOf(account.repos));

        Glide.with(this)
                .load(account.pic)
                .placeholder(R.drawable.avatar)
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .error(R.drawable.avatar)
                .addListener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        profile_icon.setImageResource(R.drawable.avatar);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        profile_icon.setImageDrawable(drawable);
                        profile_icon.setScaleX(1f);
                        profile_icon.setScaleY(1f);
                        return false;
                    }
                })
                .into(profile_icon);
    }
}