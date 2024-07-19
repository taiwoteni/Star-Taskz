package com.theteam.taskz.presentation.views;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.theteam.taskz.R;
import com.theteam.taskz.presentation.adapters.ViewPagerAdapter;
import com.theteam.taskz.presentation.viewmodels.LoginViewModel;

import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager;
    private LoginViewModel loginViewModel;


    // Inorder to specify the views we want to show
    private final ArrayList<Fragment> views = new ArrayList<>();

    @Override
    public void onBackPressed() {

        if(loginViewModel.getViewPager().getValue().getCurrentItem()==0){
            super.onBackPressed();
            return;
        }
        // We want to go back to the previous pages if we aren't on the first page.
        if(loginViewModel.getViewPager().getValue().getCurrentItem()<viewPagerAdapter.getItemCount()-1){
            loginViewModel.back();
        }
        // User's can't go back to register information after registration, so nothing happens when you have reached
        // The profile page.
        if(loginViewModel.getViewPager().getValue().getCurrentItem()==viewPagerAdapter.getItemCount()-1){
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        views.add(new NameSection());
        views.add(new BirthdaySection());
        views.add(new CategorySection());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        viewPager = (ViewPager2) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this, views);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);

        loginViewModel.setViewPager(viewPager);

    }
}