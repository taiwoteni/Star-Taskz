package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Pomodoro extends AppCompatActivity {
    TextView timerHeader;
    Button timerButtonFocusStart, timerButtonFocusPause, timerButtonFocusResume, timerButtonBreakStart, timerButtonBreakPause, timerButtonBreakResume, timerButtonEnd, timerButtonSkip, timerButtonReminder;
    ImageView hamburger_menu;
    CircularProgressBar circularProgressBar;
    SharedPreferences wakePreferences, vibratePreferences, timePreference, pausePreference, soundPreference;
    BottomSheetFragment bottomSheetFragment;
    PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    Handler handlerM, handlerA;
    Runnable runnable, runnableB;
    String feSoundName, beSoundName, wakeState, vibrateState;
    int focusTotalTime, breakTotalTime, focusMin, breakMin, focusDuration, breakDuration, pomoDuration, postponeDuration, soundDuration, durationP, pomo;
    CountDownTimer focusCountDownTimer, breakCountDownTimer;
    NumberFormat numberFormat;
    long saveMillisecondsFocus, saveMillisecondsBreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        timerHeader = findViewById(R.id.timerHeader);
        timerButtonFocusStart = findViewById(R.id.timerButtonFocusStart);
        timerButtonFocusPause = findViewById(R.id.timerButtonFocusPause);
        timerButtonFocusResume = findViewById(R.id.timerButtonFocusResume);
        timerButtonBreakStart = findViewById(R.id.timerButtonBreakStart);
        timerButtonBreakPause = findViewById(R.id.timerButtonBreakPause);
        timerButtonBreakResume = findViewById(R.id.timerButtonBreakResume);
        timerButtonEnd = findViewById(R.id.timerButtonEnd);
        timerButtonSkip = findViewById(R.id.timerButtonSkip);
        timerButtonReminder = findViewById(R.id.timerButtonReminder);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        hamburger_menu = findViewById(R.id.hamburger_menu);

        wakePreferences = getSharedPreferences("Wake_State", MODE_PRIVATE);
        vibratePreferences = getSharedPreferences("Vibrate_State", MODE_PRIVATE);
        timePreference = getSharedPreferences("timeInMinutes", MODE_PRIVATE);
        pausePreference = getSharedPreferences("Pause_State", MODE_PRIVATE);
        soundPreference = getSharedPreferences("sound_name", MODE_PRIVATE);

        focusDuration = timePreference.getInt("fdTimeNum", 10);
        breakDuration = timePreference.getInt("bdTimeNum", 10);
        pomoDuration = timePreference.getInt("lbeTimeNum", 1);
        postponeDuration = timePreference.getInt("prdTimeNum", 10);
        soundDuration = timePreference.getInt("sdTimeNum", 15);
        feSoundName = soundPreference.getString("focusSound", "Ding");
        beSoundName = soundPreference.getString("breakSound", "Ding");

        focusTotalTime = focusDuration * 60 * 1000;
        focusMin = focusDuration;
        breakTotalTime = breakDuration * 60 * 1000;
        breakMin = breakDuration;
        pomo = pomoDuration;

        timerButtonsDynamics("Focus Start");
        handlerM = new Handler();
        handlerA = new Handler();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp::MyWakeLockTag");

        circularProgressBar.setText(focusDuration + ":00");
        timerButtonReminder.setText("Remind in " + postponeDuration + " minutes");

        hamburger_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHamburgerMenu();
            }
        });

        timerButtonFocusStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFocusTimer(focusTotalTime);
            }
        });

        timerButtonFocusPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseFocusTimer();
            }
        });

        timerButtonFocusResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeFocusTimer();
            }
        });

        timerButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimer();
            }
        });

        timerButtonBreakStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breakMin = breakDuration;
                startBreakTimer(breakTotalTime);
            }
        });

        timerButtonBreakPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseBreakTimer();
            }
        });

        timerButtonBreakResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeBreakTimer();
            }
        });

        timerButtonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pomo = pomo - 1;
                Toast.makeText(Pomodoro.this, "You have skipped your break...", Toast.LENGTH_SHORT).show();
                //num = focusDuration;
                focusCountDownTimer.cancel();
                focusCountDownTimer = null;
                focusMin = focusDuration;
                startFocusTimer(focusTotalTime);
            }
        });

        timerButtonReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jet = postponeDuration * 60 * 1000;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This code will run after 10 seconds
                        Toast.makeText(Pomodoro.this, "Start your break", Toast.LENGTH_SHORT).show();
                    }
                }, jet);
            }
        });
    }

    public void startFocusTimer(long time){
        wakeState();
        stopSound();
        stopVibrator();
        timerButtonsDynamics("Focus Pause");
        timerHeader.setText("Focus");

        if (breakCountDownTimer != null){
            breakCountDownTimer.cancel();
            breakCountDownTimer = null;
        }
        focusCountDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat numberFormat = new DecimalFormat("00");
                long sec = (millisUntilFinished / 1000) % 60;
                if(sec % 60 == 59){
                    focusMin = focusMin - 1;
//                    if ((focusMin == 00 && sec == 00) || focusMin <= 00){
//                        onFinish();
//                    }else {
//
//                    }
                }
                saveMillisecondsFocus = millisUntilFinished;
                circularProgressBar.setText(focusMin + ":" + numberFormat.format(sec));
            }

            @Override
            public void onFinish() {
                playSound(feSoundName, soundDuration);
                offWakeLock();
                vibrateState();

                if (pomo == 1){
                    Toast.makeText(Pomodoro.this, "Times Up!!!", Toast.LENGTH_SHORT).show();
                    circularProgressBar.setText(focusDuration + ":00");
                    timerButtonsDynamics("Focus Start");
                }else{
                    timerButtonsDynamics("Break Start");
                }
            }
        }.start();
    }

    public void pauseFocusTimer(){
        timerButtonsDynamics("Focus Resume");
        focusCountDownTimer.cancel();
    }

    public void resumeFocusTimer(){
        timerButtonsDynamics("Focus Pause");
        startFocusTimer(saveMillisecondsFocus);
    }

    public void endTimer(){
        circularProgressBar.setText(focusDuration + ":00");
        timerButtonsDynamics("Focus Start");
        focusMin = focusDuration;
        breakMin = breakDuration;
        if (focusCountDownTimer != null){
            focusCountDownTimer.cancel();
            focusCountDownTimer = null;
        }else if (breakCountDownTimer != null){
            breakCountDownTimer.cancel();
            breakCountDownTimer = null;
        }
    }

    public void startBreakTimer(long time){
        stopSound();
        stopVibrator();
        wakeState();
        timerButtonsDynamics("Break Pause");
        timerHeader.setText("Break Time");

        focusCountDownTimer.cancel();
        focusCountDownTimer = null;
        breakCountDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat numberFormat = new DecimalFormat("00");
                long sec = (millisUntilFinished / 1000) % 60;
                if(sec % 60 == 59){
                    breakMin = breakMin - 1;
//                    if ((breakMin == 00 && sec == 00) || breakMin <= 00){
//                        onFinish();
//                    }else {
//
//                    }
                }
                saveMillisecondsBreak = millisUntilFinished;
                circularProgressBar.setText(breakMin + ":" + numberFormat.format(sec));
            }

            @Override
            public void onFinish() {
                playSound(feSoundName, soundDuration);
                offWakeLock();
                vibrateState();

                Toast.makeText(Pomodoro.this, "Break is over!!! " + focusTotalTime, Toast.LENGTH_SHORT).show();
                startFocusTimer(focusTotalTime);
                focusMin = focusDuration;
                pomo = pomo - 1;
            }
        }.start();
    }

    public void pauseBreakTimer(){
        timerButtonsDynamics("Break Resume");
        breakCountDownTimer.cancel();
    }

    public void resumeBreakTimer(){
        timerButtonsDynamics("Break Pause");
        startBreakTimer(saveMillisecondsBreak);
    }

    private void onWakeLock(){
        if (wakeLock != null && !wakeLock.isHeld()){
            wakeLock.acquire();
        }
    }

    private void offWakeLock(){
        if (wakeLock != null && wakeLock.isHeld()){
            wakeLock.release();
        }
    }

    public void wakeState(){
        wakeState = wakePreferences.getString("wakeState", "off");
        if (wakeState.equals("on")){
            onWakeLock();
        }else {
            offWakeLock();
        }
    }

    public void vibrateState(){
        vibrateState = vibratePreferences.getString("vibrateState", "off");
        if (vibrateState.equals("on")){
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    public void openHamburgerMenu(){
        bottomSheetFragment = new BottomSheetFragment(this);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        bottomSheetFragment.setCancelable(false);
    }

    public void timerButtonsDynamics(String txt) {
        if (txt.equals("Focus Start")) {
            timerButtonFocusStart.setVisibility(View.VISIBLE);
            timerButtonFocusPause.setVisibility(View.GONE);
            timerButtonFocusResume.setVisibility(View.GONE);
            timerButtonBreakStart.setVisibility(View.GONE);
            timerButtonBreakPause.setVisibility(View.GONE);
            timerButtonBreakResume.setVisibility(View.GONE);
            timerButtonEnd.setVisibility(View.GONE);
            timerButtonSkip.setVisibility(View.GONE);
            timerButtonReminder.setVisibility(View.GONE);
        }else if (txt.equals("Break Start")) {
            timerButtonFocusStart.setVisibility(View.GONE);
            timerButtonFocusPause.setVisibility(View.GONE);
            timerButtonFocusResume.setVisibility(View.GONE);
            timerButtonBreakStart.setVisibility(View.VISIBLE);
            timerButtonBreakPause.setVisibility(View.GONE);
            timerButtonBreakResume.setVisibility(View.GONE);
            timerButtonEnd.setVisibility(View.VISIBLE);
            timerButtonSkip.setVisibility(View.VISIBLE);
            timerButtonReminder.setVisibility(View.VISIBLE);
        }else if (txt.equals("Focus Resume")) {
            timerButtonFocusStart.setVisibility(View.GONE);
            timerButtonFocusPause.setVisibility(View.GONE);
            timerButtonFocusResume.setVisibility(View.VISIBLE);
            timerButtonBreakStart.setVisibility(View.GONE);
            timerButtonBreakPause.setVisibility(View.GONE);
            timerButtonBreakResume.setVisibility(View.GONE);
            timerButtonEnd.setVisibility(View.VISIBLE);
            timerButtonSkip.setVisibility(View.GONE);
            timerButtonReminder.setVisibility(View.GONE);
        }else if (txt.equals("Focus Pause")) {
            timerButtonFocusStart.setVisibility(View.GONE);
            timerButtonFocusPause.setVisibility(View.VISIBLE);
            timerButtonFocusResume.setVisibility(View.GONE);
            timerButtonBreakStart.setVisibility(View.GONE);
            timerButtonBreakPause.setVisibility(View.GONE);
            timerButtonBreakResume.setVisibility(View.GONE);
            timerButtonEnd.setVisibility(View.GONE);
            timerButtonSkip.setVisibility(View.GONE);
            timerButtonReminder.setVisibility(View.GONE);
        }else if (txt.equals("Break Resume")) {
            timerButtonFocusStart.setVisibility(View.GONE);
            timerButtonFocusPause.setVisibility(View.GONE);
            timerButtonFocusResume.setVisibility(View.GONE);
            timerButtonBreakStart.setVisibility(View.GONE);
            timerButtonBreakPause.setVisibility(View.GONE);
            timerButtonBreakResume.setVisibility(View.VISIBLE);
            timerButtonEnd.setVisibility(View.VISIBLE);
            timerButtonSkip.setVisibility(View.GONE);
            timerButtonReminder.setVisibility(View.GONE);
        }else if (txt.equals("Break Pause")) {
            timerButtonFocusStart.setVisibility(View.GONE);
            timerButtonFocusPause.setVisibility(View.GONE);
            timerButtonFocusResume.setVisibility(View.GONE);
            timerButtonBreakStart.setVisibility(View.GONE);
            timerButtonBreakPause.setVisibility(View.VISIBLE);
            timerButtonBreakResume.setVisibility(View.GONE);
            timerButtonEnd.setVisibility(View.GONE);
            timerButtonSkip.setVisibility(View.GONE);
            timerButtonReminder.setVisibility(View.GONE);
        }else {
            Toast.makeText(this, "Check Your Code Madam.", Toast.LENGTH_SHORT).show();
        }
    }

    public void playSound(String sound, int duration) {
        //Handler handler = new Handler();
        if (sound.equals("Ding")){
            mediaPlayer = MediaPlayer.create(this, R.raw.ding);
        }else if (sound.equals("Radar")){
            mediaPlayer = MediaPlayer.create(this, R.raw.radar);
        }else if (sound.equals("Alarm")){
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        }else if (sound.equals("High Pitch")){
            mediaPlayer = MediaPlayer.create(this, R.raw.high_pitch);
        }else if (sound.equals("High Pitch 2")){
            mediaPlayer = MediaPlayer.create(this, R.raw.high_pitch_b);
        }else if (sound.equals("Music Box")){
            mediaPlayer = MediaPlayer.create(this, R.raw.music_box);
        }else if (sound.equals("Ringtone")){
            mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        }else if (sound.equals("Ringtone New")){
            mediaPlayer = MediaPlayer.create(this, R.raw.ringtone_new);
        }else if (sound.equals("Ship Bell")){
            mediaPlayer = MediaPlayer.create(this, R.raw.ship_bells);
        }else {
            mediaPlayer = MediaPlayer.create(this, R.raw.ding);
        }


        int durationMilli = duration * 1000;
        handle(durationMilli);
    }

    public void handle(int durationM){
        int interval = mediaPlayer.getDuration();
        mediaPlayer.start();

        if (interval >= durationM){
            handlerM.postDelayed(runnable, durationM);
            handlerM.postDelayed(() -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                }
            }, durationM);
        }else if (interval < durationM){
            durationP = durationM - interval;

            handlerM.postDelayed(runnable, durationM);
            handlerM.postDelayed(() -> {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                handle(durationP);
                //anotherSession(durationP);
            }, interval);
        }
    }

    public void stopSound(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    public void stopVibrator(){
        if (vibrator != null){
            vibrator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}