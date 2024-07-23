package com.theteam.taskz.presentation.views;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.AuthenticationDataHolder;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.repositories.UserRepository;
import com.theteam.taskz.utils.enums.AccountType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    private ImageView back_icon, add_icon;

    private SplashRefreshLayout refresh_layout;
    private CircleImageView profile_image;
    private FrameLayout profile_layout;
    private TextInputFormField first_name_form,last_name_form,email_form,birthday_form,job_title_form;
//    job_description_form;
    private LoadableButton save;
    private Calendar birthdayCalendar;
    private UserModel user;
    private String imagePath;
    private TextView mailLabel;
    private UserRepository userRepository;

    private LinearLayout job_title_layout,job_description_layout;
    private boolean failed = true;
    private Calendar calendar;
    private int processes;
    private int completed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = new UserModel(getApplicationContext());
//        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_edit_profile);

        userRepository = new UserRepository(getApplicationContext());

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
//        job_description_form = (TextInputFormField) findViewById(R.id.job_description_form);
        job_title_layout = (LinearLayout) findViewById(R.id.job_title_layout);
        job_description_layout = (LinearLayout) findViewById(R.id.job_description_layout);
        save = (LoadableButton) findViewById(R.id.save_changes);
        mailLabel = (TextView) findViewById(R.id.mail_label);

        job_title_layout.setVisibility(View.GONE);
        job_description_layout.setVisibility(View.GONE);


//        ViewCompat.setOnApplyWindowInsetsListener(refresh_layout, (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        if (user.isGoogleAccount()){
            mailLabel.setVisibility(View.VISIBLE);
            mailLabel.setText("* Emails belonging to google accounts can't be changed");
            mailLabel.setTextColor(getResources().getColor(R.color.red));
            email_form.setEnabled(false);
            email_form.setAlpha(0.5f);
        }
        if (user.hasBirthday()){
            birthdayCalendar = user.birthday();
        }

        if(user.hasProfile()){
            add_icon.setScaleX(0.8f);
            add_icon.setScaleY(0.8f);
            add_icon.setImageResource(R.drawable.edit);
            Glide.with(getApplicationContext())
                    .load(user.profile())
                    .placeholder(R.drawable.avatar)
                    .transition(DrawableTransitionOptions.withCrossFade(1500))
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            profile_image.setImageDrawable(drawable);
                            profile_image.setScaleX(1);
                            profile_image.setScaleY(1);
                            return true;
                        }
                    }).into(profile_image);
        }

        first_name_form.setText(user.firstName());
        last_name_form.setText(user.lastName());
        email_form.setText(user.email());
        if(user.isGoogleAccount()){
            email_form.setEnabled(false);
        }
        if(user.accountType() == AccountType.Business){
            job_title_layout.setVisibility(View.VISIBLE);
            job_title_form.setText(user.jobTitle());
//            job_description_layout.setVisibility(View.VISIBLE);
//            job_description_form.setText(user.jobDescription());
        }

        if(birthdayCalendar != null){
            final SimpleDateFormat birthdayFormat = new SimpleDateFormat("MMMM, dd", Locale.getDefault());
            birthday_form.setText(birthdayFormat.format(birthdayCalendar.getTime()));
        }
        birthday_form.setOnClickListener(view -> showDatePicker());


        save.setOnClickListener(view -> {
            saveChanges();
        });

        back_icon.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void saveChanges(){
        processes = 0;
        final boolean nameChanged = !Objects.equals(user.fullName(), first_name_form.getText().trim() + " " + last_name_form.getText().trim());
        final boolean emailChanged = !user.email().equals(email_form.getText().trim());
        final boolean profileChanged = imagePath!=null;
        final boolean jobChanged = !user.jobTitle().equals(job_title_form.getText().trim());

        if(nameChanged){
            processes++;
        }
        if(emailChanged){
            processes++;
        }
        if(profileChanged){
            processes++;
        }
        if(jobChanged){
            processes++;
        }

        if (processes!=0){
            refresh_layout.startAnimating();
            save.startLoading();
            saveName();
        }

    }

    private void saveName(){
        final boolean nameChanged = !Objects.equals(user.fullName(), first_name_form.getText().trim() + " " + last_name_form.getText().trim());
        if(!nameChanged){
            saveEmail();
            return;
        }
        userRepository.updateNames(
                first_name_form.getText().trim(),
                last_name_form.getText().trim(),
                jsonObject -> {
                    HashMap<String,Object> hash = user.toJson();
                    hash.put("firstName", first_name_form.getText().trim());
                    hash.put("lastName", last_name_form.getText().trim());

                    UserModel.saveUserData(hash, getApplicationContext());
                    user = new UserModel(getApplicationContext());
                    recourse();
                    saveEmail();

                },
                errorListener


        );

    }
    private Response.ErrorListener errorListener = volleyError -> {
        Log.e("API_RESPONSE", volleyError.toString());
        refresh_layout.stopAnimating();
        failed = true;
    };

    private void saveEmail(){
        final boolean emailChanged = !user.email().equals(email_form.getText().trim());
        if(!emailChanged){
            saveJobTitle();
            return;
        }
        userRepository.updateEmail(
                email_form.getText().trim(),
                jsonObject -> {
                    HashMap<String,Object> hash = user.toJson();
                    hash.put("email", email_form.getText().trim());
                    hash.put("password", user.password());

                    UserModel.saveUserData(hash, getApplicationContext());
                    user = new UserModel(getApplicationContext());
                    recourse();
                    saveJobTitle();

                },
                errorListener

        );

    }
    private void saveJobTitle() {
        final boolean jobChanged = !user.jobTitle().equals(job_title_form.getText().trim());
        if(!jobChanged){
            saveBirthday();
            return;
        }
        userRepository.updateJobTitle(
                job_title_form.getText().trim(),
                jsonObject -> {
                    HashMap<String,Object> hash = user.toJson();
                    hash.put("jobTitle", job_title_form.getText().trim());
                    hash.put("password", user.password());

                    UserModel.saveUserData(hash, getApplicationContext());
                    user = new UserModel(getApplicationContext());
                    recourse();
                    saveBirthday();

                },
                errorListener

        );

    }
    private void saveBirthday(){
        final boolean birthdayChanged = birthdayCalendar != null && birthdayCalendar.get(Calendar.DAY_OF_YEAR) != user.birthday().get(Calendar.DAY_OF_YEAR);
        if(!birthdayChanged){
            saveProfile();
            return;
        }
        userRepository.updateBirthday(
                birthdayCalendar,
                jsonObject -> {
                    HashMap<String,Object> hash = user.toJson();
                    hash.put("dateOfBirth", AuthenticationDataHolder.dob);
                    hash.put("password", user.password());

                    UserModel.saveUserData(hash, getApplicationContext());
                    user = new UserModel(getApplicationContext());
                    recourse();
                    saveProfile();

                },
                errorListener

        );

    }

    private void saveProfile(){
        final boolean profileChanged = imagePath!=null;

        if (!profileChanged){
            recourse();
            return;
        }
        userRepository.uploadProfilePic(
                user.uid(),
                imagePath,
                null,
                url -> {
                    HashMap<String,Object> hash = user.toJson();
                    hash.put("profilePicture", url);
                    hash.put("password", user.password());

                    UserModel.saveUserData(hash, getApplicationContext());
                    user = new UserModel(getApplicationContext());
                    recourse();

                },
                errorListener

        );

    }

    private void recourse(){
        completed++;
        if(completed==processes){
            refresh_layout.stopAnimating();
            new Handler().postDelayed(this::finish,2000);

        }
    }

    void showDatePicker(){
        Calendar calendar = birthdayCalendar!=null? ((Calendar) birthdayCalendar.clone()):Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog
                (this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int _year, int _month, int day) {
                        calendar.set(Calendar.MONTH, _month);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                        String dayString = String.valueOf(day);
                        String monthString = String.valueOf(_month+1);

                        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
                        final SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        AuthenticationDataHolder.dob = secondFormat.format(calendar.getTime());
                        birthday_form.setText(dateFormat.format(calendar.getTime()));
                        birthdayCalendar = calendar;
                    }
                },year,month,dayOfMonth);
        datePickerDialog.show();
    }





}