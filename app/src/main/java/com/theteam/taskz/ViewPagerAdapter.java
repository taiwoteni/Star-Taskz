package com.theteam.taskz;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        // Return the fragment instance for the given position
        switch (position) {
            case 0:
                return new NameSection();
            default:
                return new EmailSection();
        }

    }

    @Override
    public int getItemCount() {
        // Return the total number of fragments
        return 2; // Adjust as needed based on the number of fragments you have
    }
}

