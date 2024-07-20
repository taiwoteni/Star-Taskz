package com.theteam.taskz.presentation.views;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.utils.enums.AccountType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    private ImageView back_icon, add_icon;

    private SplashRefreshLayout refresh_layout;
    private CircleImageView profile_image;
    private FrameLayout profile_layout;
    private TextInputFormField first_name_form,last_name_form,email_form,birthday_form,job_title_form, job_description_form;
    private LoadableButton save;
    private Calendar birthdayCalendar;
    private UserModel user;

    private LinearLayout job_title_layout,job_description_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new UserModel(getApplicationContext());
        birthdayCalendar = user.birthday();
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_edit_profile);

        back_icon = (ImageView) findViewById(R.id.goBackToPreviousPage);
        refresh_layout = (SplashRefreshLayout) findViewById(R.id.main);
        profile_image = (CircleImageView) findViewById(R.id.profile_icon);
        profile_layout = (FrameLayout) findViewById(R.id.profile_layout);
        add_icon = (ImageView) findViewById(R.id.add_icon);
        first_name_form = (TextInputFormField) findViewById(R.id.first_name_form);
        last_name_form = (TextInputFormField) findViewById(R.id.last_name_form);
        email_form = (TextInputFormField) findViewById(R.id.email_form);
        birthday_form = (TextInputFormField) findViewById(R.id.birthday_form);
        job_title_form = (TextInputFormField) findViewById(R.id.job_title_form);
        job_description_form = (TextInputFormField) findViewById(R.id.job_description_form);
        job_title_layout = (LinearLayout) findViewById(R.id.job_title_layout);
        job_description_layout = (LinearLayout) findViewById(R.id.job_description_layout);
        save = (LoadableButton) findViewById(R.id.save_changes);

        job_title_layout.setVisibility(View.GONE);
        job_description_layout.setVisibility(View.GONE);


        ViewCompat.setOnApplyWindowInsetsListener(refresh_layout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        first_name_form.setText(user.firstName());
        last_name_form.setText(user.lastName());
        email_form.setText(user.email());
        if(user.isGoogleAccount()){
            email_form.setEnabled(false);
        }
        if(user.accountType() == AccountType.Business){
            job_title_layout.setVisibility(View.VISIBLE);
            job_title_form.setText(user.jobTitle());
            job_description_layout.setVisibility(View.VISIBLE);
            job_description_form.setText(user.jobDescription());
        }

        if(birthdayCalendar != null){
            final SimpleDateFormat birthdayFormat = new SimpleDateFormat("MMMM, dd", Locale.getDefault());
            birthday_form.setText(birthdayFormat.format(birthdayCalendar.getTime()));
        }


        save.setOnClickListener(view -> {
            saveChanges();
        });

        back_icon.setOnClickListener(view -> {
            finish();
        });
    }

    private void saveChanges(){
        save.startLoading();
        new Handler().postDelayed(() -> {
            save.stopLoading();
            runOnUiThread(() -> {
                refresh_layout.startAnimating();
                new Handler().postDelayed(() -> {
                    refresh_layout.stopAnimation();
                    finish();
                },5000);
            });
        },2000);

    }
}