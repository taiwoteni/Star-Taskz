package com.theteam.taskz.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.theteam.taskz.data.StateHolder;
import com.theteam.taskz.TasksPageFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskPageAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> views = new ArrayList<>();

    public TaskPageAdapter(Fragment fragment, ArrayList<Calendar> dates) {
        super(fragment);
        ArrayList<Fragment> views = new ArrayList<>();
        //We first initialize the List of TaskPageFragments
        StateHolder.taskPageFragments = new ArrayList<>();
        for(final Calendar calendar: dates){
            //Then we declare an instantiated TaskPageFragment, inorder to add the same Object to both the views array and
            // TaskPageFragments
            final TasksPageFragment tasksPageFragment = new TasksPageFragment(calendar);
            views.add(tasksPageFragment);
            StateHolder.taskPageFragments.add(tasksPageFragment);
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

