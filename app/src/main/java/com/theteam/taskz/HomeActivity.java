package com.theteam.taskz;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theteam.taskz.home_pages.CalendarFragment;
import com.theteam.taskz.home_pages.FocusFragment;
import com.theteam.taskz.home_pages.MoreFragment;
import com.theteam.taskz.home_pages.TaskFragment;
import com.theteam.taskz.home_pages.WeatherFragment;

import java.util.ArrayList;

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
        views.add(new FocusFragment());
//        views.add(new WeatherFragment());
//        views.add(new MoreFragment());


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


    }

    int getFragmentIndex(final int menuItemId){
        switch (menuItemId){
            case R.id.navigation_tasks:
                return 0;
            case R.id.navigation_calender:
                return 1;
            case R.id.navigation_focus:
                return 2;
//            case R.id.navigation_weather:
//                return 3;
            default:
                return 2;
        }
    }
    int getNavBarItemIndex(int position) {
        switch (position) {
            case 0:
                return R.id.navigation_tasks;
            case 1:
                return R.id.navigation_calender;
//            case 3:
//                return R.id.navigation_weather;
//            case 3:
//                return R.id.navigation_settings;
            default:
                return R.id.navigation_focus;
        }
    }

    void showErrorMessage(final String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}