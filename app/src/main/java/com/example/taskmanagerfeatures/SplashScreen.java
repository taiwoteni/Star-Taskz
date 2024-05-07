package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    Animation fade_in, bottom_down, wave;
    LinearLayout background;
    ImageView taskzImage;
    TextView taskzTxt;
    int duration = 2000;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        background = findViewById(R.id.background);
        taskzImage = findViewById(R.id.taskzImage);
        taskzTxt = findViewById(R.id.taskzTxt);
//        handler = new Handler();
//
//        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//        bottom_down = AnimationUtils.loadAnimation(this, R.anim.bottom_down);
//        wave = AnimationUtils.loadAnimation(this, R.anim.wave);
//        background.startAnimation(wave);
//
//        bottom_down.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        taskzImage.startAnimation(fade_in);
//                        taskzTxt.startAnimation(fade_in);
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startActivity(new Intent(SplashScreen.this, ExpandorCollapseLayout.class));
//                            }
//                        }, 1000);
//                    }
//                }, 500);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
    }
}