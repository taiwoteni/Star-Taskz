package com.theteam.taskz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView app_name;
    private View lottie;

    private boolean startedAnimation = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app_name = (TextView) findViewById(R.id.app_name);
        lottie = (LottieAnimationView) findViewById(R.id.loading_lottie);

        startAnimation();



    }

    private void startAnimation(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(2000);

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setDuration(2000);

                app_name.setAnimation(fadeOut);
                runOnUiThread(() -> {
                    hideAppName();
                    showLottie();
                });
                lottie.setAnimation(fadeIn);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(i);
                        cancel();
                        finish();
                    }
                }, 5000);
            }
        };

        new Timer().schedule(task, 5000);




    }

    void showLottie(){
        lottie.setVisibility(View.VISIBLE);

    }

    void hideAppName(){
        app_name.setVisibility(View.GONE);

    }
}