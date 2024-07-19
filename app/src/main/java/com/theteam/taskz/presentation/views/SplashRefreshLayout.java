package com.theteam.taskz.presentation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.theteam.taskz.R;

public class SplashRefreshLayout extends FrameLayout {

    private RelativeLayout relativeLayout;
    private View splashLayout;

    private LottieAnimationView lottie;

    private int ANIMATION_TYPE = 0;

    public SplashRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            init(context, attrs);
        }
        catch (AndroidRuntimeException e){
            e.printStackTrace();
        }
    }

    public SplashRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            init(context, attrs);
        }
        catch (AndroidRuntimeException e){
            e.printStackTrace();
        }
    }

    private void init(Context context, AttributeSet attr) {

        // Initialize the RelativeLayout and add it to the FrameLayout
        relativeLayout = new RelativeLayout(context);
        addView(relativeLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        if(attr != null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attr, R.styleable.SplashRefreshLayout);
            ANIMATION_TYPE = typedArray.getInt(R.styleable.SplashRefreshLayout_animation, 0);
            // If splash wave screen
            if(ANIMATION_TYPE == 0){
                splashLayout = LayoutInflater.from(context).inflate(R.layout.splash_wave_layout, relativeLayout,false);}
            else{
                splashLayout = LayoutInflater.from(context).inflate(R.layout.splash_loading_layout, relativeLayout,false);
            }
            lottie = splashLayout.findViewById(R.id.splash_lottie);


            RelativeLayout.LayoutParams presetChildParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            splashLayout.setOnClickListener(view -> {
                // this is empty, was done intentionally to prevent
                // the xml child from being clickable when the splashLayout is loading
            });
            splashLayout.setVisibility(GONE); // Default to GONE
            relativeLayout.addView(splashLayout, presetChildParams);
            typedArray.recycle();
        }

    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (relativeLayout.getChildCount() > 1) {
            throw new IllegalStateException("SplashRefreshLayout can host only one direct child in the RelativeLayout");
        }
        if(getChildCount()<1){
            super.addView(child,index,params);
        }
        else{
            // Add the child to the RelativeLayout
            relativeLayout.addView(child, 0, params);
        }

    }

    public void startAnimating() {
        splashLayout.setVisibility(View.VISIBLE);
        lottie.loop(ANIMATION_TYPE != 0);
        lottie.playAnimation();
    }

    public void stopAnimation(){
        lottie.pauseAnimation();
        splashLayout.setVisibility(View.GONE);
    }
}

