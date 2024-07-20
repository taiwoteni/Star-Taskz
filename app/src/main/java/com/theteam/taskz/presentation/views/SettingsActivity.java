package com.theteam.taskz.presentation.views;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.utils.others.ThemeManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private boolean remindersEnabled, notificationsEnabled;
    private SwitchMaterial notificaions_switch, reminders_switch;

    private CircleImageView profile_icon;

    private TextView name_text;

    private RelativeLayout edit_profile_layout,weather_layout,pomodorro_layout,platform_sync_layout,notifications_layout,reminders_layout,ringtone_layout,logout_layout;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        remindersEnabled = settings.getBoolean("reminders", true);
        notificationsEnabled = settings.getBoolean("notifications", true);


        edit_profile_layout = findViewById(R.id.edit_profile_layout);
        weather_layout = findViewById(R.id.weather_layout);
        pomodorro_layout = findViewById(R.id.pomodorro_layout);
        notifications_layout = findViewById(R.id.notifications_layout);
        reminders_layout = findViewById(R.id.reminders_layout);
        ringtone_layout = findViewById(R.id.ringtone_layout);
        logout_layout = findViewById(R.id.logout_layout);
        notificaions_switch = findViewById(R.id.notifications_switch);
        reminders_switch = findViewById(R.id.reminder_switch);
        name_text = findViewById(R.id.name_text);
        profile_icon = findViewById(R.id.profile_icon);
        platform_sync_layout = findViewById(R.id.platform_sync_layout);

        final UserModel user = new UserModel(getApplicationContext());
        name_text.setText(user.fullName());
        if(user.hasProfile()){
            profile_icon.setScaleX(1f);
            profile_icon.setScaleY(1f);
            profile_icon.setImageURI(Uri.parse(user.profile()));
        }
        else{
            profile_icon.setScaleX(1.5f);
            profile_icon.setScaleY(1.5f);
            profile_icon.setImageResource(R.drawable.avatar);
        }


        reminders_switch.setChecked(remindersEnabled);
        notificaions_switch.setChecked(notificationsEnabled);
        handleCheckUI(reminders_switch, remindersEnabled);
        handleCheckUI(notificaions_switch, notificationsEnabled);

        reminders_layout.setOnClickListener(view -> {
            reminders_switch.setChecked(!reminders_switch.isChecked());
        });
        notifications_layout.setOnClickListener(view -> {
            notificaions_switch.setChecked(!notificaions_switch.isChecked());
        });

        edit_profile_layout.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), EditProfile.class));
        });
        platform_sync_layout.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), PlatformSync.class));
        });

        findViewById(R.id.back).setOnClickListener(view -> {
            finish();
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        notificaions_switch.setOnCheckedChangeListener((compoundButton, b) -> {
            handleCheckUI(notificaions_switch, b);
            settings.edit().putBoolean("notifications", b).apply();
        });
        reminders_switch.setOnCheckedChangeListener((compoundButton, b) -> {
            handleCheckUI(reminders_switch, b);
            settings.edit().putBoolean("reminders", b).apply();
        });
    }

    private void handleCheckUI(SwitchMaterial target_switch, boolean isChecked){
        final ThemeManager themeManager = new ThemeManager(getApplicationContext());
        target_switch.setThumbTintList(ColorStateList.valueOf(isChecked? Color.WHITE:getColor(R.color.themeColor)));
        target_switch.setTrackTintList(ColorStateList.valueOf(isChecked?getColor(R.color.themeColor): themeManager.tertiary));
    }
}