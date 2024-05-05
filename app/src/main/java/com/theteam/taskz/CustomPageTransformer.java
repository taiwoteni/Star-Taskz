package com.theteam.taskz;

import android.view.View;
import android.view.animation.BounceInterpolator;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

public class CustomPageTransformer implements ViewPager2.PageTransformer {

    private static float DEFAULT_ALPHA = 1f;
    private static int DEFAULT_TRANSITION_DURATION = 5000; // Change this value as needed

    public CustomPageTransformer(final float defaultAlpha, final int transitionMillis){
        DEFAULT_ALPHA = defaultAlpha;
        DEFAULT_TRANSITION_DURATION=transitionMillis;
    }
    public CustomPageTransformer(){

    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        // You can customize the transition animation here
        page.setAlpha(DEFAULT_ALPHA); // Example animation

        // Change the transition duration
        page.animate().setInterpolator(new BounceInterpolator()).setDuration(DEFAULT_TRANSITION_DURATION).start();


    }
}