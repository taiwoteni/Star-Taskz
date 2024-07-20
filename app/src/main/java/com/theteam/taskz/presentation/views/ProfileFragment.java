package com.theteam.taskz.presentation.views;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private ImageView settings_icon,message_icon, share_icon;
    private TextView name_text,job_text;
    private CircleImageView profile_image;

    private UserModel user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_view_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = new UserModel(requireActivity());

        settings_icon = view.findViewById(R.id.settings_icon);
        message_icon = (ImageView) view.findViewById(R.id.message_icon);
        share_icon = (ImageView) view.findViewById(R.id.share_icon);
        name_text = (TextView) view.findViewById(R.id.name_text);
        job_text = (TextView) view.findViewById(R.id.job_title_text);
        profile_image = view.findViewById(R.id.profile_icon);

        if(user.hasProfile()){
            Glide.with(this)
                    .load(user.profile())
                    .placeholder(R.drawable.avatar)
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .error(R.drawable.avatar)
                    .addListener(new RequestListener<Drawable>() {

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            profile_image.setImageResource(R.drawable.avatar);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            profile_image.setImageDrawable(drawable);
                            profile_image.setScaleX(1f);
                            profile_image.setScaleY(1f);
                            return false;
                        }
                    })
                    .into(profile_image);
        }

        name_text.setText(user.fullName());
        job_text.setText(user.jobTitle());

        // that is, if the profile currently viewed is the user's profile
        if(user.uid().equals(new UserModel(requireActivity()).uid())){
            message_icon.setImageResource(R.drawable.edit);
            message_icon.setColorFilter(requireActivity().getResources().getColor(R.color.themeColor), PorterDuff.Mode.SRC_IN);
            message_icon.setOnClickListener(view1 -> {
                startActivity(new Intent(requireActivity().getApplicationContext(), EditProfile.class));
            });
        }
        else{
            message_icon.setImageResource(R.drawable.message_bubble_outlined);
            message_icon.setColorFilter(requireActivity().getResources().getColor(R.color.themeColor), PorterDuff.Mode.SRC_IN);
            message_icon.setOnClickListener(view1 -> {
            });
        }



        settings_icon.setOnClickListener(view1 -> {
            startActivity(new Intent(view.getContext(), SettingsActivity.class));
        });
    }
}