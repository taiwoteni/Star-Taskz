package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;

public class ProgressBarTesting extends AppCompatActivity {
    CircularProgressIndicator progressBar;
    TextView textViewProgress;
    Button pause, play;
    CountDownTimer countDownTimer;
    int progress, num, totalTime, y;
    long saveMilliseconds;
    String which;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar_testing);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progressBar);
        textViewProgress = findViewById(R.id.textViewProgress);
        pause = findViewById(R.id.pause);
        play = findViewById(R.id.play);

        totalTime = 30000;
        startCountDown();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueTime();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTime();
                Toast.makeText(ProgressBarTesting.this, String.valueOf(y), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startCountDown(){
        countDownTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long sec = (millisUntilFinished / 1000) % 60;
                progress = (int) ((millisUntilFinished / (float) totalTime) * 100);
                progressBar.setProgress(progress);
                textViewProgress.setText(00 + ":" + f.format(sec));
                saveMilliseconds = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                Toast.makeText(ProgressBarTesting.this, "Times Up", Toast.LENGTH_SHORT).show();
            }
        };

        countDownTimer.start();
    }

    public void continueTime(){
        totalTime = (int) saveMilliseconds;
        startCountDown();
    }

    public void pauseTime(){
        countDownTimer.cancel();
        //progressBar.clearAnimation();
    }
}