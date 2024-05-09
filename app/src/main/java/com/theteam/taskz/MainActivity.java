package com.theteam.taskz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.sql.Time;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView app_name;
    private View lottie;

    private boolean startedAnimation = false;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    // Call this method from your activity or fragment
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SYSTEM_ALERT_WINDOW}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    // Override onRequestPermissionsResult to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can post notifications
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestNotificationPermission();

        AlarmManager.speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    for(final Voice v: AlarmManager.speech.getVoices()){
                        Log.v("VOICES", v.getName());
                    }
                    final int res = AlarmManager.speech.setLanguage(new Locale("en", "US"));
                    AlarmManager.speech.setPitch(0.25f);
                    AlarmManager.speech.setSpeechRate(1.5f);

                    if(res == TextToSpeech.LANG_MISSING_DATA || res==TextToSpeech.LANG_NOT_SUPPORTED){
                        AlarmManager.speech.setLanguage(Locale.ENGLISH);
                        AlarmManager.speech.setVoice(new Voice("eng-USA",Locale.ENGLISH, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_LOW,true, null));
                    }
                    else{
                        final Voice voice = new Voice("eng-USA",new Locale("en", "US"), Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH,true, null);
                        AlarmManager.speech.setVoice(voice);
                    }
//                    AlarmManager.speech.speak("Welcome",TextToSpeech.QUEUE_ADD, null,null);

                }
                else{
                    AlarmManager.speech=null;

                }
            }
        });

        app_name = (TextView) findViewById(R.id.app_name);
        lottie = (LottieAnimationView) findViewById(R.id.loading_lottie);

        startAnimation();



    }

    void showMessage(final String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                        Intent i = new Intent(getApplicationContext(),HomeActivity.class);
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