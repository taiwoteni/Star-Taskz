package com.example.taskmanagerfeatures;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    TextView bottom_drawer_done, focus_duration_number, break_duration_number, pomo_number, postpone_reminder_number, sound_duration_number, bottom_drawer_back;
    TextView focus_ending_sound, break_ending_sound;
    ImageView goto_focus_duration, goto_break_duration, goto_long_break_every, goto_postpone_reminder, goto_focus_ending, goto_break_ending, goto_sound_duration;
    SwitchCompat screen_on_switch, vibrate_on_switch;
    FocusDurationDialog focusDurationDialog;
    SoundsBottomFragment soundsBottomFragment;
    SharedPreferences preferences, preferences2, preferences3, preferences4;
    Activity activity;
    int fdTimeNumber, bdTimeNumber, pTimeNumber, prTimeNumber, sdTimeNumber;
    String wake_state, vibrate_state, wakeChecked, vibrateChecked, feName, beName;
    boolean sound_switch_checked, vibrate_switch_checked;
    public BottomSheetFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_drawer_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottom_drawer_done = view.findViewById(R.id.bottom_drawer_done);
        bottom_drawer_back = view.findViewById(R.id.bottom_drawer_back);
        focus_duration_number = view.findViewById(R.id.focus_duration_number);
        break_duration_number = view.findViewById(R.id.break_duration_number);
        pomo_number = view.findViewById(R.id.pomo_number);
        postpone_reminder_number = view.findViewById(R.id.postpone_reminder_number);
        sound_duration_number = view.findViewById(R.id.sound_duration_number);
        goto_focus_duration = view.findViewById(R.id.goto_focus_duration);
        goto_break_duration = view.findViewById(R.id.goto_break_duration);
        goto_long_break_every = view.findViewById(R.id.goto_long_break_every);
        goto_postpone_reminder = view.findViewById(R.id.goto_postpone_reminder);
        screen_on_switch = view.findViewById(R.id.screen_on_switch);
        goto_focus_ending = view.findViewById(R.id.goto_focus_ending);
        goto_break_ending = view.findViewById(R.id.goto_break_ending);
        goto_sound_duration = view.findViewById(R.id.goto_sound_duration);
        vibrate_on_switch = view.findViewById(R.id.vibrate_on_switch);
        focus_ending_sound = view.findViewById(R.id.focus_ending_sound);
        break_ending_sound = view.findViewById(R.id.break_ending_sound);

        preferences = activity.getSharedPreferences("timeInMinutes", Context.MODE_PRIVATE);
        preferences2 = activity.getSharedPreferences("Wake_State", Context.MODE_PRIVATE);
        preferences3 = activity.getSharedPreferences("Vibrate_State", Context.MODE_PRIVATE);
        preferences4 = activity.getSharedPreferences("sound_name", Context.MODE_PRIVATE);

        fdTimeNumber = preferences.getInt("fdTimeNum", 10);
        bdTimeNumber = preferences.getInt("bdTimeNum", 10);
        pTimeNumber = preferences.getInt("lbeTimeNum", 1);
        prTimeNumber = preferences.getInt("prdTimeNum", 10);
        sdTimeNumber = preferences.getInt("sdTimeNum", 15);
        wake_state = preferences2.getString("wakeState", "off");
        vibrate_state = preferences3.getString("vibrateState", "off");
        feName = preferences4.getString("focusSound", "Ding");
        beName = preferences4.getString("breakSound", "Ding" );

        if (wake_state.equals("on")){
            sound_switch_checked = true;
        }else if (wake_state.equals("off")){
            sound_switch_checked = false;
        }

        if (vibrate_state.equals("on")){
            vibrate_switch_checked = true;
        }else if (vibrate_state.equals("off")){
            vibrate_switch_checked = false;
        }

        focus_duration_number.setText(fdTimeNumber + " minutes");
        break_duration_number.setText(bdTimeNumber + " minutes");
        pomo_number.setText(pTimeNumber + " pomo");
        postpone_reminder_number.setText(prTimeNumber + " minutes");
        sound_duration_number.setText(sdTimeNumber + " seconds");
        screen_on_switch.setChecked(sound_switch_checked);
        vibrate_on_switch.setChecked(vibrate_switch_checked);
        focus_ending_sound.setText(feName);
        break_ending_sound.setText(beName);

        goto_focus_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFocusDurationFragment("Focus Duration", "minutes");
            }
        });

        goto_break_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFocusDurationFragment("Break Duration", "minutes");
            }
        });

        goto_long_break_every.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFocusDurationFragment("Long Break Every", "pomo");
            }
        });

        goto_postpone_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFocusDurationFragment("Postpone Reminder Duration", "minutes");
            }
        });

        screen_on_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    wake_state = "on";
                }else{
                    wake_state = "off";
                }
            }
        });

        goto_focus_ending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSoundBottomFragment("Focus-ending Sound");
            }
        });

        goto_break_ending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSoundBottomFragment("Break-ending Sound");
            }
        });

        goto_sound_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFocusDurationFragment("Sound Duration", "seconds");
            }
        });

        vibrate_on_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    vibrate_state = "on";
                }else{
                    vibrate_state = "off";
                }
            }
        });

        bottom_drawer_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        bottom_drawer_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences2 = activity.getSharedPreferences("Wake_State", Context.MODE_PRIVATE);
                SharedPreferences preferences3 = activity.getSharedPreferences("Vibrate_State", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences2.edit();
                SharedPreferences.Editor editor2 = preferences3.edit();
                if (wake_state.equals("on")){
                    editor.putString("wakeState", "on");
                    editor.apply();
                    Toast.makeText(activity, "on", Toast.LENGTH_SHORT).show();
                }else if (wake_state.equals("off")){
                    editor.putString("wakeState", "off");
                    editor.apply();
                    Toast.makeText(activity, "off", Toast.LENGTH_SHORT).show();
                }

                if (vibrate_state.equals("on")){
                    editor2.putString("vibrateState", "on");
                    editor2.apply();
                }else if(vibrate_state.equals("off")){
                    editor2.putString("vibrateState", "off");
                    editor2.apply();
                }
                activity.recreate();
                dismiss();
            }
        });
    }

    public void openFocusDurationFragment(String head, String denom){
        dismiss();
        focusDurationDialog = new FocusDurationDialog(activity, head, denom);
        focusDurationDialog.show(getParentFragmentManager(), focusDurationDialog.getTag());
        focusDurationDialog.setCancelable(false);
    }

    public void openSoundBottomFragment(String head){
        dismiss();
        soundsBottomFragment = new SoundsBottomFragment(activity, head);
        soundsBottomFragment.show(getParentFragmentManager(), soundsBottomFragment.getTag());
        soundsBottomFragment.setCancelable(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        screen_on_switch.setChecked(sound_switch_checked);
        vibrate_on_switch.setChecked(vibrate_switch_checked);
    }

    @Override
    public void onResume() {
        super.onResume();
        screen_on_switch.setChecked(sound_switch_checked);
        vibrate_on_switch.setChecked(vibrate_switch_checked);
    }
}
