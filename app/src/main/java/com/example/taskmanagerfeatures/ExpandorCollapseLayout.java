package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ExpandorCollapseLayout extends AppCompatActivity {
    RelativeLayout timeLayout, placeLayout, reminderLayout;
    LinearLayout timeContentLayout, placeContentLayout;
    TextView timeExit, idToday, timePicker, placeExit, idPickAPlace, idAddPresentPlaces, cancelReminder, saveReminder, reminder;
    GradientDrawable timeBackground, placeBackground;
    CalendarView calenderView;
    TimePicker timeView;
    ImageView favouriteReminder, checkEditText, cameraEditText, reminderIcon;
    EditText reminderTitle;
    SharedPreferences reminderPreference;
    boolean isTimeExpanded = false, isPlaceExpanded = false, isCalenderExpanded = false, isClockExpanded = false, isFavourite = false;
    float [] half, whole;
    String title, category_name, category_colour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandor_collapse_layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        timeLayout = findViewById(R.id.timeLayout);
        timeContentLayout = findViewById(R.id.timeContentLayout);
        timeExit = findViewById(R.id.timeExit);
        calenderView = findViewById(R.id.calenderView);
        idToday = findViewById(R.id.idToday);
        timeView = findViewById(R.id.timeView);
        timePicker = findViewById(R.id.timePicker);
        placeLayout = findViewById(R.id.placeLayout);
        placeContentLayout = findViewById(R.id.placeContentLayout);
        idPickAPlace = findViewById(R.id.idPickAPlace);
        idAddPresentPlaces = findViewById(R.id.idAddPresentPlaces);
        cancelReminder = findViewById(R.id.cancelReminder);
        saveReminder = findViewById(R.id.saveReminder);
        favouriteReminder = findViewById(R.id.favouriteReminder);
        checkEditText = findViewById(R.id.checkEditText);
        cameraEditText = findViewById(R.id.cameraEditText);
        reminderTitle = findViewById(R.id.reminderTitle);
        placeExit = findViewById(R.id.placeExit);
        reminderLayout = findViewById(R.id.reminderLayout);
        reminder = findViewById(R.id.reminder);
        reminderIcon = findViewById(R.id.reminderIcon);

        calenderView.setVisibility(View.GONE);
        timeView.setVisibility(View.GONE);

        half = new float[]{50, 50, 50, 50, 0, 0, 0, 0};
        whole = new float[]{50, 50, 50, 50, 50, 50, 50, 50};

        timeBackground = (GradientDrawable) timeLayout.getBackground();
        placeBackground = (GradientDrawable) placeLayout.getBackground();
        timeBackground.setCornerRadii(whole);
        placeBackground.setCornerRadii(whole);

        reminderPreference = getSharedPreferences("Selected_Reminder", MODE_PRIVATE);
        category_name = reminderPreference.getString("category_name", "My Reminder");
        category_colour = reminderPreference.getString("category_colour", "Red");
        setReminderNameAndColour();

        timeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandTime();
            }
        });

        timeExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeContentLayout.setVisibility(View.GONE);
                timeExit.setVisibility(View.GONE);
                isTimeExpanded = false;
                timeBackground.setCornerRadii(whole);
            }
        });

        idToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCalender();
            }
        });

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandClock();
                //timeView.setVisibility(View.VISIBLE);
            }
        });

        placeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandPlace();
            }
        });

        placeExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeContentLayout.setVisibility(View.GONE);
                placeExit.setVisibility(View.GONE);
                isPlaceExpanded = false;
                placeBackground.setCornerRadii(whole);
            }
        });

        cancelReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpandorCollapseLayout.this, MainActivity.class));
            }
        });

        saveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = reminderTitle.getText().toString();
                Toast.makeText(ExpandorCollapseLayout.this, title + " has been saved!!!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ExpandorCollapseLayout.this, MainActivity.class));
            }
        });

        favouriteReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavourite){
                    favouriteReminder.setColorFilter(null);
                    isFavourite = false;
                }else if (!isFavourite){
                    favouriteReminder.setColorFilter((ContextCompat.getColor(v.getContext(), R.color.green3)));
                    isFavourite = true;
                }
            }
        });

        reminderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpandorCollapseLayout.this, SelectedCategory.class));
            }
        });
    }

    public void setReminderNameAndColour(){
        reminder.setText(category_name);

        if (category_colour.equals("Red")){
            reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.redO)));
        }else if (category_colour.equals("Orange")){
            reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.orange)));
        }else if (category_colour.equals("Yellow")){
            reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.yellow)));
        }else if (category_colour.equals("Lime")){
           reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.lime)));
        }else if (category_colour.equals("Sky_Blue")){
            reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.skyBlue)));
        }else if (category_colour.equals("Blue")){
            reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.blue3)));
        }else if (category_colour.equals("Pink")){
            reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.purPink)));
        }else if (category_colour.equals("Purple")){
            reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.purple)));
        }else {
            reminderIcon.setColorFilter((ContextCompat.getColor(this, R.color.blue)));
        }
    }

    public void expandTime(){
        if (!isTimeExpanded){
            timeContentLayout.setVisibility(View.VISIBLE);
            timeExit.setVisibility(View.VISIBLE);
            isTimeExpanded = true;
            timeBackground.setCornerRadii(half);
        }else {
            timeContentLayout.setVisibility(View.GONE);
            timeExit.setVisibility(View.GONE);
            isTimeExpanded = false;
            timeBackground.setCornerRadii(whole);
        }
    }

    public void expandPlace(){
        if (!isPlaceExpanded){
            placeContentLayout.setVisibility(View.VISIBLE);
            placeExit.setVisibility(View.VISIBLE);
            isPlaceExpanded = true;
            placeBackground.setCornerRadii(half);
        }else {
            placeContentLayout.setVisibility(View.GONE);
            placeExit.setVisibility(View.GONE);
            isPlaceExpanded = false;
            placeBackground.setCornerRadii(whole);
        }
    }

    public void expandCalender(){
        if (isCalenderExpanded){
            calenderView.setVisibility(View.GONE);
            isCalenderExpanded = false;
        }else if (!isCalenderExpanded){
            calenderView.setVisibility(View.VISIBLE);
            timeView.setVisibility(View.GONE);
            isCalenderExpanded = true;
            isClockExpanded = false;
        }
    }

    public void expandClock(){
        if (isClockExpanded){
            timeView.setVisibility(View.GONE);
            isClockExpanded = false;
        }else if (!isClockExpanded){
            timeView.setVisibility(View.VISIBLE);
            calenderView.setVisibility(View.GONE);
            isClockExpanded = true;
            isCalenderExpanded = false;
        }
    }
}