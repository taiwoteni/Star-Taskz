package com.theteam.taskz;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theteam.taskz.adapters.ViewPagerAdapter;
import com.theteam.taskz.data.AuthenticationDataHolder;
import com.theteam.taskz.data.StateHolder;
import com.theteam.taskz.home_pages.AIFragment;
import com.theteam.taskz.home_pages.CalendarFragment;
import com.theteam.taskz.home_pages.FocusFragment;
import com.theteam.taskz.home_pages.TaskFragment;
import com.theteam.taskz.models.UserModel;
import com.theteam.taskz.services.ApiService;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.UnderlineTextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView bottomNavigationView;

    private final ArrayList<Fragment> views = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        views.add(new TaskFragment());
        views.add(new CalendarFragment());
        views.add(new AIFragment());
        views.add(new FocusFragment());
        if(StateHolder.mediaPlayer != null){
            if(StateHolder.mediaPlayer.isPlaying()){
                StateHolder.mediaPlayer.pause();
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this, views);

        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setPageTransformer(new CustomPageTransformer());
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.setSelectedItemId(getNavBarItemIndex(position));
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                viewPager.setCurrentItem(getFragmentIndex(menuItem.getItemId()), true);

                return true;
            }
        });
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                viewPager.setCurrentItem(getFragmentIndex(menuItem.getItemId()), true);
            }
        });

        final String desc = "com.theteam.taskz.STAR_AI";
        Intent shortcutIntent = new Intent(getApplicationContext(),HomeActivity.class);
        shortcutIntent.putExtra("ai", "");
        shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.setAction(Intent.ACTION_DEFAULT);

        ShortcutInfoCompat shortcut =  new ShortcutInfoCompat.Builder(getApplicationContext(), "STAR_AI")
                .setCategories(Collections.singleton(desc))
                .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.star_square))
                .setIntent(shortcutIntent)
                .setLongLived(true)
                .setShortLabel("Star AI✨")
                .build();
        ShortcutManagerCompat.pushDynamicShortcut(getApplicationContext(), shortcut);

        checkIntro();
    }

    int getFragmentIndex(final int menuItemId){
        switch (menuItemId){
            case R.id.navigation_tasks:
                return 0;
            case R.id.navigation_calender:
                return 1;
            case R.id.navigation_ai:
                return 2;
            default:
                return 3;
        }
    }
    int getNavBarItemIndex(int position) {
        switch (position) {
            case 0:
                return R.id.navigation_tasks;
            case 1:
                return R.id.navigation_calender;
            case 2:
                return R.id.navigation_ai;
            default:
                return R.id.navigation_focus;
        }
    }

    private void checkIntro(){
        final UserModel model = new UserModel(this);

        if(getIntent().hasExtra("first")){
            Dialog dialog = new Dialog(HomeActivity.this);

            View contentView = getLayoutInflater().inflate(R.layout.star_intro_dialog, null);
            final LoadableButton loadableButton = contentView.findViewById(R.id.go_button);
            final UnderlineTextView skipButton = contentView.findViewById(R.id.skip_button);

            loadableButton.setOnClickListener(view -> {
                dialog.dismiss();
                viewPager.setCurrentItem(2, true);
            });

            skipButton.setOnClickListener(view -> {
                getIntent().removeExtra("first");
                dialog.dismiss();
            });

            dialog.setContentView(contentView);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
            dialog.setCancelable(false);
            dialog.show();

        }
        else if(getIntent().hasExtra("logged in")){
            try {
                loadTasks();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (model.tokenExpiration().getTime().before(Calendar.getInstance().getTime())){
            refreshToken();
        }
        else{
            checkIfSynced();
            if(getIntent().hasExtra("ai")){
                viewPager.setCurrentItem(2,false);
            }
        }

    }

    private void refreshToken(){
        final UserModel model = new UserModel(this);
        AuthenticationDataHolder.email = model.email();
        AuthenticationDataHolder.password = model.password();
        new ApiService(this).refreshToken();


    }

    private void loadTasks() throws JSONException {
        if(getIntent().hasExtra("logged in")){
            new ApiService(this,getLayoutInflater()).saveTasks(true);
        }
    }

    private void checkIfSynced(){
        if(new UserModel(this).needsSync()){
            new UserModel(this).setNeedsToSync(false);
            new ApiService(this,getLayoutInflater()).checkAndSynced();
        }

    }

    private void showErrorMessage(final String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}