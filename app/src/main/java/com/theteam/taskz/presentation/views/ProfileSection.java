package com.theteam.taskz.presentation.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.theteam.taskz.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileSection extends Fragment {

    private LoadableButton loadableButton;
    private FrameLayout profile_layout;
    private CircleImageView profile_icon;

    private LottieAnimationView splash_lottie;

    public ProfileSection() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profile_icon = (CircleImageView) view.findViewById(R.id.profile_icon);
        profile_layout = (FrameLayout) view.findViewById(R.id.profile_layout);
        loadableButton = (LoadableButton) view.findViewById(R.id.loadable_button);
        splash_lottie = (LottieAnimationView) view.findViewById(R.id.splash_lottie);

        loadableButton.setOnClickListener(view1 -> {
            splash_lottie.setAnimation(R.raw.loading_splash);
            splash_lottie.setSpeed(1.25f);
            splash_lottie.playAnimation();
        });




    }
}