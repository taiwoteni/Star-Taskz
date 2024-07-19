package com.theteam.taskz.presentation.adapters;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.theteam.taskz.presentation.views.EmailSection;
import com.theteam.taskz.presentation.views.JobSection;
import com.theteam.taskz.presentation.views.ProfileSection;

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

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addJobSection(){
        Log.v("VIEW_PAGER_ADAPTER", getItemCount() + "");
        // If other sections were added previously, i.e
        // If the user came back to change from Business to another account or he came back to the page,
        // then we remove all and add only the profile and email section
        if(getItemCount()>3){
            for(int i = getItemCount()-1; views.size()>3; i--){
                views.remove(i);
                notifyItemRemoved(i);
                Log.v("VIEW_PAGER_ADAPTER", "Removed " + (i+1));
            }
            Log.v("VIEW_PAGER_ADAPTER", "Now "+getItemCount());
        }
        views.add(new JobSection());
        views.add(new EmailSection());
        views.add(new ProfileSection());
        notifyItemRangeInserted(3,getItemCount());
    }
    public void useNormalSection(){
        Log.v("VIEW_PAGER_ADAPTER", getItemCount() + "");
        // If other sections were added previously, i.e
        // If the user came back to change from Business to another account,
        // then we remove all and add only the profile and email section
        if(getItemCount()>3){
            for(int i = getItemCount()-1; views.size()>3; i--){
                views.remove(i);
                notifyItemRemoved(i);
                Log.v("VIEW_PAGER_ADAPTER", "Removed " + (i+1));
            }
            Log.v("VIEW_PAGER_ADAPTER", "Now "+getItemCount());
        }
        // Profile Section is meant to be added as well
        views.add(new EmailSection());
        views.add(new ProfileSection());
        notifyItemRangeInserted(3,getItemCount());

    }



}

