package com.theteam.taskz;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskPageAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> views = new ArrayList<>();

    public TaskPageAdapter(Fragment fragment, ArrayList<Calendar> dates) {
        super(fragment);
        ArrayList<Fragment> views = new ArrayList<>();
        for(final Calendar calendar: dates){
            views.add(new TasksPageFragment(calendar));
        }
        
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

