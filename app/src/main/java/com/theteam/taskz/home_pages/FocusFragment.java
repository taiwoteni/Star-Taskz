package com.theteam.taskz.home_pages;

import static android.content.Context.MODE_PRIVATE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.theteam.taskz.R;
import com.theteam.taskz.utilities.ThemeManager;
import com.theteam.taskz.view_models.LoadableButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FocusFragment extends Fragment {
    private enum TimeState{
        Started,
        Paused,
        Resumed,
        Stoppped
    }
    private TimeState state = TimeState.Stoppped;

    private TextView start_text,stop_text,pomodorro_progress_text,not_started;
    private LinearLayout start_layout,stop_layout;
    private CircularProgressIndicator pomodorro_progress, inward_progress;
    private ImageView start_icon,stop_icon;

    private SharedPreferences wakePreferences, vibratePreferences, timePreference, pausePreference, soundPreference;
    private ImageView settings_icon;

    private String feSoundName, beSoundName, wakeState, vibrateState;
    private int focusTotalTime, breakTotalTime, focusMin, breakMin, focusDuration, breakDuration, pomoDuration, postponeDuration, soundDuration, durationP, pomo;

    private CountDownTimer focusCountDownTimer, breakCountDownTimer;


    private NumberFormat numberFormat;
    private long intitialSetMilliseconds, saveMillisecondsFocus, saveMillisecondsBreak;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private int selectedMinutes;
    private int focusSeconds;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private Handler handlerM, handlerA;
    private Runnable runnable, runnableB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.focus_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        start_icon = view.findViewById(R.id.start_icon);
        stop_icon = view.findViewById(R.id.stop_icon);
        start_layout = view.findViewById(R.id.start_layout);
        stop_layout = view.findViewById(R.id.stop_layout);
        pomodorro_progress = view.findViewById(R.id.pomodoro_progress_bar);
        pomodorro_progress_text = view.findViewById(R.id.pomodoro_progress_text);
        start_text = view.findViewById(R.id.start_text);
        stop_text = view.findViewById(R.id.stop_text);
        not_started = view.findViewById(R.id.not_started_text);
        inward_progress = view.findViewById(R.id.inward_progress_bar);
        settings_icon = view.findViewById(R.id.settings_icon);

        wakePreferences = view.getContext().getApplicationContext().getSharedPreferences("Wake_State", MODE_PRIVATE);
        vibratePreferences = view.getContext().getApplicationContext().getSharedPreferences("Vibrate_State", MODE_PRIVATE);
        timePreference = view.getContext().getApplicationContext().getSharedPreferences("timeInMinutes", MODE_PRIVATE);
        pausePreference = view.getContext().getApplicationContext().getSharedPreferences("Pause_State", MODE_PRIVATE);
        soundPreference = view.getContext().getApplicationContext().getSharedPreferences("sound_name", MODE_PRIVATE);

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

        timerButtonsDynamics();
        handlerM = new Handler();
        handlerA = new Handler();
        powerManager = (PowerManager) view.getContext().getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp::MyWakeLockTag");

        pomodorro_progress_text.setText(focusDuration + ":00");

        settings_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFocusSettings(focusDuration,focusSeconds);
            }
        });

        start_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state == TimeState.Stoppped){
                    startFocusTimer(focusTotalTime, false);
                }
                else if(state == TimeState.Resumed || state == TimeState.Started){
                    pauseFocusTimer();
                }
                else{
                    resumeFocusTimer();
                }
            }
        });
        stop_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimer();
            }
        });


    }

    void showFocusSettings(int initialMinutes, int initialSeconds){
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.focus_settings_layout, null, false);

        final NumberPicker minutesPicker = (NumberPicker) v.findViewById(R.id.minutes_picker);
        final NumberPicker secondsPicker = (NumberPicker) v.findViewById(R.id.seconds_picker);
        final LoadableButton loadableButton = (LoadableButton) v.findViewById(R.id.loadable_button);

        int selectedMinutes = initialMinutes;
        int selectedSeconds = initialSeconds;


        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(60);
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);

        minutesPicker.setValue(timePreference.getInt("setMinute", 10));
        secondsPicker.setValue(timePreference.getInt("setSecond", 00));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            minutesPicker.setTextColor(getResources().getColor(R.color.themeColor));
            minutesPicker.setTextSize(getResources().getDimensionPixelSize(R.dimen.medium_font_size));
            secondsPicker.setTextSize(getResources().getDimensionPixelSize(R.dimen.medium_font_size));
            secondsPicker.setTextColor(getResources().getColor(R.color.themeColor));

        }



        loadableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadableButton.startLoading();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadableButton.stopLoading();
                        final int minute = minutesPicker.getValue();
                        final int second = secondsPicker.getValue();

                        timePreference.edit().putInt("setMinute", minute).apply();
                        timePreference.edit().putInt("setSecond", second).apply();
                        focusDuration = minute;
                        focusMin = focusDuration;
                        focusSeconds = second;
                        pomodorro_progress_text.setText(focusDuration + ":" + (String.valueOf(focusSeconds).length() == 1? "0"+focusSeconds:focusSeconds));
                        focusTotalTime = (focusDuration * 60 * 1000) + ((focusSeconds +1)*1000);
                        dialog.cancel();

                    }
                },2500);
            }
        });



        dialog.setContentView(v);
        dialog.setCancelable(true);
        dialog.setDismissWithAnimation(true);
        dialog.show();
    }

    void startFocusTimer(long time, boolean resuming){
        state = resuming? TimeState.Resumed: TimeState.Started;
        wakeState();
        stopSound();
        stopVibrator();
        timerButtonsDynamics();
        pomodorro_progress.setMax((int)(resuming?intitialSetMilliseconds:time));
//        timerHeader.setText("Focus");

        if(!resuming){
            intitialSetMilliseconds = time;
            showPomodoroNotification(false);
        }
        if (breakCountDownTimer != null){
            breakCountDownTimer.cancel();
            breakCountDownTimer = null;
        }
        focusCountDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat numberFormat = new DecimalFormat("00");
                long sec = (millisUntilFinished / 1000) % 60;
                if (sec % 60 == 59) {
                    focusMin = focusMin == 0? 0: focusMin-1;
//                    if ((focusMin == 00 && sec == 00) || focusMin <= 00){
//                        onFinish();
//                    }else {
//
//                    }
                }

                if(resuming){
                    pomodorro_progress.setProgress((int)(intitialSetMilliseconds-millisUntilFinished), true);
                }
                else{
                    pomodorro_progress.setProgress((int)(time-millisUntilFinished), true);
                }
                saveMillisecondsFocus =  millisUntilFinished;
                timePreference.edit().putInt("fdTimeNum", focusMin).apply();
                pomodorro_progress_text.setText(focusMin + ":" + numberFormat.format(sec));
                not_started.setTextColor(getResources().getColor(R.color.themeColor));
                not_started.setText("Ends In " + focusMin + " minutes");

                if(millisUntilFinished> time/2){
                    pomodorro_progress.setIndicatorColor(getResources().getColor(R.color.themeColor));
                    inward_progress.setIndicatorColor(getResources().getColor(R.color.themeColor));
                }
                if(millisUntilFinished<= time/2 && millisUntilFinished>time/4){
                    pomodorro_progress.setIndicatorColor(getResources().getColor(R.color.yellow));
                    inward_progress.setIndicatorColor(getResources().getColor(R.color.yellow));
                }
                if(millisUntilFinished <= time/4){
                    pomodorro_progress.setIndicatorColor(getResources().getColor(R.color.red));
                    inward_progress.setIndicatorColor(getResources().getColor(R.color.red));

                }

            }


            @Override
            public void onFinish() {
                playSound(soundDuration);
                offWakeLock();
                vibrateState();
                pomodorro_progress.setProgress((int)time, true);
                pomodorro_progress_text.setText(focusDuration + ":00");
                state = TimeState.Stoppped;
                timerButtonsDynamics();
                showPomodoroNotification(true);
                endTimer();

//                if (pomo == 1){
//                    showMessage("Time Up!!");
//                    pomodorro_progress_text.setText(focusDuration + ":00");
//                    state = TimeState.Stoppped;
//                    timerButtonsDynamics();
//                }else{
//                    state = TimeState.Resumed;
//                    timerButtonsDynamics();
//                }
            }
        }.start();
    }

    void pauseFocusTimer(){
        state = TimeState.Paused;
        timerButtonsDynamics();
        focusCountDownTimer.cancel();
    }

    void resumeFocusTimer(){
        state = TimeState.Resumed;
        timerButtonsDynamics();
        startFocusTimer(saveMillisecondsFocus, true);
    }
    void onWakeLock(){
        if (wakeLock != null && !wakeLock.isHeld()){
            wakeLock.acquire();
        }
    }

    void endTimer(){
        state = TimeState.Stoppped;
        pomodorro_progress_text.setText(focusDuration + ":00");
        timePreference.edit().putInt("fdTimeNum", timePreference.getInt("setMinute", 10)).apply();
        pomodorro_progress.setProgress((int)intitialSetMilliseconds,true);
        timerButtonsDynamics();
        saveMillisecondsFocus = 0;
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
    void offWakeLock(){
        if (wakeLock != null && wakeLock.isHeld()){
            wakeLock.release();
        }
    }

    //To refresh the wakeState from the SharedPreferences
    //And calls it wether on or Off.
    void wakeState(){
        wakeState = wakePreferences.getString("wakeState", "off");
        if (wakeState.equals("on")){
            onWakeLock();
        }else {
            offWakeLock();
        }
    }

    void vibrateState()  {
        vibrateState = vibratePreferences.getString("vibrateState", "off");
        if (vibrateState.equals("on")){
            vibrator = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }

    //Inorder to configure the buttons to show the expected states
    //...When the TimeState changes
    void timerButtonsDynamics() {
        final ThemeManager theme = new ThemeManager(requireActivity());
        if(state == TimeState.Stoppped){
            //To set the stop layout to disabled
            stop_layout.setBackgroundTintList(ColorStateList.valueOf(theme.tertiary));
            stop_icon.setColorFilter(theme.secondary);
            stop_text.setTextColor(theme.secondary);
            stop_layout.setEnabled(false);

            //To set the start layout to enabled and start mode
            start_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
            start_icon.setColorFilter(getResources().getColor(R.color.white));
            start_text.setTextColor(getResources().getColor(R.color.white));
            start_icon.setImageResource(R.drawable.play);
            start_text.setText("Start");

            not_started.setTextColor(theme.secondary);
            not_started.setText("Not Started");
        }
        if(state == TimeState.Started || state == TimeState.Resumed){
            //To set the stop layout to enabled
            stop_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            stop_icon.setColorFilter(getResources().getColor(R.color.white));
            stop_text.setTextColor(getResources().getColor(R.color.white));
            stop_layout.setEnabled(true);

            //To set the start layout to enabled and pause mode
            start_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            start_icon.setColorFilter(getResources().getColor(R.color.white));
            start_text.setTextColor(getResources().getColor(R.color.white));
            start_icon.setImageResource(R.drawable.pause);
            start_text.setText("Pause");

            not_started.setTextColor(getResources().getColor(R.color.themeColor));
            not_started.setText("Ends In " + focusDuration + " minutes");
        }
        if(state == TimeState.Paused){
            //To set the stop layout to enabled
            stop_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            stop_icon.setColorFilter(getResources().getColor(R.color.white));
            stop_text.setTextColor(getResources().getColor(R.color.white));
            stop_layout.setEnabled(true);

            //To set the start layout to enabled and pause mode
            start_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            start_icon.setColorFilter(getResources().getColor(R.color.white));
            start_text.setTextColor(getResources().getColor(R.color.white));
            start_icon.setImageResource(R.drawable.play);
            start_text.setText("Resume");

            not_started.setTextColor(getResources().getColor(R.color.themeColor));
            not_started.setText("Paused");

        }

    }

    void stopSound(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    void playSound(int duration) {
        mediaPlayer = MediaPlayer.create(getActivity(), Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(false);
        handle(mediaPlayer.getDuration());
    }

    void handle(int durationM){
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

    void stopVibrator(){
        if (vibrator != null){
            vibrator.cancel();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    void showMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showPomodoroNotification(boolean finished){
        Bitmap largeIcon = BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), R.drawable.taskz_round);

        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel taskChannel = new NotificationChannel("POMODORO", "Star Taskz", NotificationManager.IMPORTANCE_HIGH);
        taskChannel.setDescription("Task Focus Channel");
        Uri soundUri = Uri.parse("android.resource://" + getActivity().getApplicationContext().getPackageName() + "/" + R.raw.notification);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build();

        taskChannel.setSound(soundUri, audioAttributes);
        nm.createNotificationChannel(taskChannel);


        Notification notif = new NotificationCompat.Builder(getActivity().getApplicationContext(), "POMODORO")
                .setContentTitle("Focus")
                .setContentText(finished? "Time Up!!":"Your Focus Timer will end in " + focusDuration + (focusDuration==1?" minute": " minutes"))
                .setSmallIcon(R.drawable.task)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setOngoing(false)
                .setSound(soundUri)
                .setChannelId("POMODORO")
                .setColor(getActivity().getApplicationContext().getResources().getColor(R.color.themeColor))
                .setAutoCancel(true)
                .build();
        nm.notify(0, notif);
    }

}
