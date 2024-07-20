package com.theteam.taskz.presentation.views;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theteam.taskz.R;
import com.theteam.taskz.presentation.adapters.ViewPagerAdapter;
import com.theteam.taskz.data.models.AuthenticationDataHolder;
import com.theteam.taskz.data.models.StateHolder;
import com.theteam.taskz.presentation.viewmodels.LoginViewModel;
import com.theteam.taskz.presentation.viewmodels.SplashViewModel;
import com.theteam.taskz.presentation.viewmodels.TaskDatesViewModel;
import com.theteam.taskz.presentation.viewmodels.TasksViewModel;
import com.theteam.taskz.presentation.viewmodels.WorkspacesViewModel;
import com.theteam.taskz.utils.enums.AccountType;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.repositories.ApiService;
import com.theteam.taskz.utils.others.ThemeManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    private SplashRefreshLayout splash_layout;
    private ViewPagerAdapter viewPagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private AccountType account = AccountType.Business;
    private final ArrayList<Fragment> views = new ArrayList<>();
    private TasksViewModel tasksViewModel;
    private TaskDatesViewModel taskDatesViewModel;
    private SplashViewModel splashViewModel;
    private WorkspacesViewModel workspacesViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the respective providers to be used in the sub fragments
        // of this Activity
        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        taskDatesViewModel = new ViewModelProvider(this).get(TaskDatesViewModel.class);
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        workspacesViewModel = new ViewModelProvider(this).get(WorkspacesViewModel.class);

        // To pause the alarm ringing media player if the app when activity opened
        if(StateHolder.mediaPlayer != null){
            if(StateHolder.mediaPlayer.isPlaying()){
                StateHolder.mediaPlayer.pause();
            }
        }


        views.add(new TasksFragment());
        if(account!=AccountType.Personal){
            views.add(new CollaborationFragment());
        }
        views.add(new AIFragment());
        // This is only meant to be added if it is personal
        // Else, it's meant to be profile fragment
        if(account==AccountType.Personal){
            // Here it's meant to be settings fragment only for personal accounts
            views.add(new FocusFragment());
        }else{
            views.add(new ProfileFragment());
        }

        // View initialization
        splash_layout = findViewById(R.id.splash_layout);
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        viewPager = findViewById(R.id.view_pager);
        //Adapter initialization
        viewPagerAdapter = new ViewPagerAdapter(this, views);

        // To inflate the menu based on account type
        bottomNavigationView.inflateMenu(
                account==AccountType.Personal?R.menu.bottom_nav_menu_personal:
                        account==AccountType.Business? R.menu.bottom_nav_menu_business:R.menu.bottom_nav_menu_family);

        // To set the initial page and orientation of view pager
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);

        // We set the home splash layout used in the provider
        // across the sub-fragments to this splash layout
        splashViewModel.setHomeSplashLayout(splash_layout);


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


        // TO automatically create a Shortcut once activity opens
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
        final boolean isPersonal = account == AccountType.Personal;
        final boolean isDark = new ThemeManager(this).isDarkMode();
        bottomNavigationView.setBackgroundTintList(ColorStateList.valueOf(new ThemeManager(this).rootBackground));
        switch (menuItemId){
            case R.id.navigation_plans:
            case R.id.navigation_projects:
            case R.id.navigation_tasks:
                bottomNavigationView.setBackgroundTintList(ColorStateList.valueOf(new ThemeManager(this).background));
                return 0;
            case R.id.navigation_family:
            case R.id.navigation_collaboration:
                return 1;
            case R.id.navigation_ai:
                return isPersonal? 1:2;
            case R.id.navigation_focus:
                return 2;
            case R.id.navigation_profile:
                bottomNavigationView.setBackgroundTintList(ColorStateList.valueOf(new ThemeManager(this).background));
                return 3;
            default:
                return 3;
        }
    }
    int getNavBarItemIndex(int position) {
        final boolean isBusiness = account == AccountType.Business;
        final boolean isFamily = account == AccountType.Family;
        final boolean isPersonal = account == AccountType.Personal;
        switch (position) {
            case 0:
                return isBusiness?R.id.navigation_projects:(isFamily? R.id.navigation_plans:R.id.navigation_tasks);
            case 1:
                return isBusiness?R.id.navigation_collaboration:(isFamily? R.id.navigation_family:R.id.navigation_ai);
            case 2:
                return isPersonal? R.id.navigation_focus:R.id.navigation_ai;
            default:
                return isPersonal? R.id.navigation_setting:R.id.navigation_profile;
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
        else {
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



    private void showErrorMessage(final String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}