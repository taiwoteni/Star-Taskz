package com.theteam.taskz.adapters;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> views = new ArrayList<>();

    public ViewPagerAdapter(FragmentActivity fragmentActivity, ArrayList<Fragment> views) {
        super(fragmentActivity);
        this.views = views;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("FRAGMENT", "Opened");
        return views.get(position);
    }

    @Override
    public int getItemCount() {
        // Return the total number of fragments
        return views.size(); // Adjust as needed based on the number of fragments you have
    }
}

