package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity{
    TextView timerHeader;
    Button timerButtonStart, timerButtonEnd, timerButtonSkip, timerButtonReminder;
    ImageView hamburger_menu;
    BottomSheetFragment bottomSheetFragment;
    boolean isTimerRunning = false, isBreakRunning = false;
    long sec, saveMilliSeconds, saveMilliSeconds2;
    CountDownTimer countDownTimer, breakCountDownTimer;
    CircularProgressBar circularProgressBar;
    int min, num, pas, totalTime, breakTime, focusDuration, breakDuration, pomoDuration, postponeDuration, soundDuration, saveProgress, breakNum, durationP;
    SharedPreferences sharedPreferences, preferences, timePreference, pausePreference, soundPreference;
    String wakeState, vibrateState, pauseState, pauseWhich, stopWhich, feSoundName, beSoundName;
    private PowerManager.WakeLock wakeLock;
    PowerManager powerManager;
    Vibrator vibrator;
    MediaPlayer mediaPlayer;
    Handler handlerM, handlerA;
    Runnable runnable, runnableB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        timerHeader = findViewById(R.id.timerHeader);
        timerButtonStart = findViewById(R.id.timerButtonStart);
        timerButtonEnd = findViewById(R.id.timerButtonEnd);
        timerButtonSkip = findViewById(R.id.timerButtonSkip);
        timerButtonReminder = findViewById(R.id.timerButtonReminder);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        hamburger_menu = findViewById(R.id.hamburger_menu);

        sharedPreferences = getSharedPreferences("Wake_State", MODE_PRIVATE);
        preferences = getSharedPreferences("Vibrate_State", MODE_PRIVATE);
        timePreference = getSharedPreferences("timeInMinutes", MODE_PRIVATE);
        pausePreference = getSharedPreferences("Pause_State", MODE_PRIVATE);
        soundPreference = getSharedPreferences("sound_name", MODE_PRIVATE);

        handlerM = new Handler();
        handlerA = new Handler();
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp::MyWakeLockTag");

        focusDuration = timePreference.getInt("fdTimeNum", 10);
        breakDuration = timePreference.getInt("bdTimeNum", 10);
        pomoDuration = timePreference.getInt("lbeTimeNum", 1);
        postponeDuration = timePreference.getInt("prdTimeNum", 10);
        soundDuration = timePreference.getInt("sdTimeNum", 15);
        feSoundName = soundPreference.getString("focusSound", "Ding");
        beSoundName = soundPreference.getString("breakSound", "Ding");

        timerButtonReminder.setText("Remind in " + postponeDuration + " minutes");

        pauseWhich = "focus";
        stopWhich = "focus";
        min = focusDuration;
        num = focusDuration;
        pas = focusDuration;
        hourNumber();
        totalTime = min * 60 * 1000;
        breakTime = breakDuration * 60 * 1000;

        hamburger_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning) {
                    pauseTimer("focus");
                }else if (isBreakRunning){
                    pauseTimer("break");
                }
                openFragment();
            }
        });

        timerButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSound();

                String btn = timerButtonStart.getText().toString();
                if (btn.equals("Start Break")){
                    breakTimer();
                }else {
                    if (isTimerRunning) {
                        pauseTimer("focus");
                    }else if (isBreakRunning){
                        pauseTimer("break");
                    }else if (!isTimerRunning && pauseWhich.equals("focus")){
                        startTimer();
                    }else if (!isBreakRunning && pauseWhich.equals("break")){
                        breakTimer();
                    }
                }
            }
        });

        timerButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning){
                    stopTimer("focus");
                }else if (isBreakRunning){
                    stopTimer("break");
                }else if (!isTimerRunning && stopWhich.equals("focus")){
                    stopTimer("focus");
                }else if (!isBreakRunning && stopWhich.equals("break")){
                    stopTimer("break");
                }
                offWakeLock();
            }
        });

        timerButtonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pomoDuration = pomoDuration - 1;
                Toast.makeText(MainActivity.this, "You have skipped your break...", Toast.LENGTH_SHORT).show();
                num = focusDuration;
                startTimer();
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
                        Toast.makeText(MainActivity.this, "Answer our question jare!!!", Toast.LENGTH_SHORT).show();
                    }
                }, jet);
            }
        });
    }

    public void hourNumber(){
        circularProgressBar.setText(min + ":00");
    }

    public void startTimer(){
        timerFeatures();

        SharedPreferences.Editor editor = pausePreference.edit();
        editor.putString("pauseState", "off");
        editor.commit();
        countDownTimer = new CountDownTimer(totalTime, 1000){
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                sec = (millisUntilFinished / 1000) % 60;
                float secFloat = (float) totalTime;
                int progress = (int) ((millisUntilFinished / secFloat) * 100);
                circularProgressBar.setProgress(progress);
                if(sec % 60 == 59){
                    num = num - 1;
                    if (num == 00 && sec == 00){
                        onFinish();
                    }
                }
                circularProgressBar.setText(num + ":" + f.format(sec));
                saveMilliSeconds = millisUntilFinished;
                saveProgress = progress;
            }

            public void onFinish() {
                playSound(feSoundName, soundDuration);

                if (pomoDuration == 1){
                    circularProgressBar.setProgress(0);
                    Toast.makeText(MainActivity.this, "Times Up!!!", Toast.LENGTH_SHORT).show();
                    timerButtonsDynamics("Start");
                    num = num + 1;
                }else{
                    timerButtonsDynamics("Start Break");
                }

                hourNumber();
                offWakeLock();
                isTimerRunning = false;
                if (vibrateState.equals("on")){
                    vibrateDevice();
                }
            }
        }.start();
        timerButtonsDynamics("Pause");
        timerHeader.setText("Focus");
        isTimerRunning = true;
    }

    public void pauseTimer(String which){
        SharedPreferences.Editor editor = pausePreference.edit();
        editor.putString("pauseState", "on");
        editor.commit();

        if (which.equals("break")){
            if (breakCountDownTimer != null) {
                breakTime = (int) saveMilliSeconds2;
                //num = num + 1;
                breakCountDownTimer.cancel();
                isBreakRunning = false;
                pauseWhich = "break";
//            circularProgressBar.stopAnimation();
            }
        }else if (which.equals("focus")){
            if (countDownTimer != null) {
                totalTime = (int) saveMilliSeconds;
                //num = num + 1;
                countDownTimer.cancel();
                isTimerRunning = false;
                pauseWhich = "focus";
//            circularProgressBar.stopAnimation();
            }
        }

        timerButtonsDynamics("Resume");
    }

    public void stopTimer(String which){
        Toast.makeText(MainActivity.this, "Timer has stopped.", Toast.LENGTH_SHORT).show();
        timerButtonsDynamics("Start");
        hourNumber();
        totalTime = min * 60 * 1000;
        num = min;

        if (which.equals("break")){
            stopWhich = "break";
            breakCountDownTimer.cancel();
            breakCountDownTimer = null;
        }else if (which.equals("focus")){
            stopWhich = "focus";
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void breakTimer(){
        timerFeatures();
        num = breakDuration;

        breakCountDownTimer = new CountDownTimer(breakTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                sec = (millisUntilFinished / 1000) % 60;
                float secFloat = (float) totalTime;
                int progress = (int) ((millisUntilFinished / secFloat) * 100);
                circularProgressBar.setProgress(progress);
                if(sec % 60 == 59){
                    num = num - 1;
                    if (num == 00 && sec == 00){
                        onFinish();
                    }
                }
                circularProgressBar.setText(num + ":" + f.format(sec));
                saveMilliSeconds2 = millisUntilFinished;
                //saveProgress = progress;
            }

            @Override
            public void onFinish() {
                playSound(beSoundName, soundDuration);
                pomoDuration = pomoDuration - 1;
                Toast.makeText(MainActivity.this, "Your break has ended", Toast.LENGTH_SHORT).show();
                isBreakRunning = false;
                num = focusDuration;
                startTimer();
            }
        }.start();
        timerButtonsDynamics("Pause");
        timerHeader.setText("Break");
        isBreakRunning = true;
    }

    public void timerButtonsDynamics(String txt){
       if (txt.equals("Start")){
           timerButtonStart.setText(txt);
           timerButtonStart.setBackgroundColor((ContextCompat.getColor(this, R.color.green)));
           timerButtonEnd.setVisibility(View.INVISIBLE);
           timerButtonEnd.setEnabled(false);
           timerButtonSkip.setVisibility(View.INVISIBLE);
           timerButtonSkip.setEnabled(false);
           timerButtonReminder.setVisibility(View.INVISIBLE);
           timerButtonReminder.setEnabled(false);
       }else if (txt.equals("Resume")){
           timerButtonStart.setText(txt);
           timerButtonStart.setBackgroundColor((ContextCompat.getColor(this, R.color.grey)));
           timerButtonEnd.setVisibility(View.VISIBLE);
           timerButtonEnd.setEnabled(true);
           timerButtonSkip.setVisibility(View.INVISIBLE);
           timerButtonSkip.setEnabled(false);
           timerButtonReminder.setVisibility(View.INVISIBLE);
           timerButtonReminder.setEnabled(false);
       }else if (txt.equals("Pause")){
           timerButtonStart.setText("Pause");
           timerButtonStart.setBackgroundColor((ContextCompat.getColor(this, R.color.black)));
           timerButtonEnd.setVisibility(View.INVISIBLE);
           timerButtonEnd.setEnabled(false);
           timerButtonSkip.setVisibility(View.INVISIBLE);
           timerButtonSkip.setEnabled(false);
           timerButtonReminder.setVisibility(View.INVISIBLE);
           timerButtonReminder.setEnabled(false);
       }else if(txt.equals("Start Break")){
           timerButtonStart.setText("Start Break");
           timerButtonStart.setBackgroundColor((ContextCompat.getColor(this, R.color.green)));
           timerButtonEnd.setVisibility(View.VISIBLE);
           timerButtonEnd.setEnabled(true);
           timerButtonSkip.setVisibility(View.VISIBLE);
           timerButtonSkip.setEnabled(true);
           timerButtonReminder.setVisibility(View.VISIBLE);
           timerButtonReminder.setEnabled(true);
       }else {
           Toast.makeText(this, "Check Your Code Madam.", Toast.LENGTH_SHORT).show();
       }
    }

    public void openFragment(){
        bottomSheetFragment = new BottomSheetFragment(this);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        bottomSheetFragment.setCancelable(false);
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

    private void vibrateDevice(){
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(200);
            }
        }
    }

    public void timerFeatures(){
        wakeState = sharedPreferences.getString("wakeState", "off");
        vibrateState = preferences.getString("vibrateState", "off");
        //pauseState = pausePreference.getString("pauseState", "off");

        if (wakeState.equals("on")){
            onWakeLock();
        }else {
            offWakeLock();
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
                //Toast.makeText(this, String.valueOf(durationP), Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                handle(durationP);
                //anotherSession(durationP);
            }, interval);
        }
    }

    public void anotherSession(int time){
        handlerA.postDelayed(runnableB, time);
        handlerA.postDelayed(() -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
        }, time);
    }

    public void stopSound(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
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