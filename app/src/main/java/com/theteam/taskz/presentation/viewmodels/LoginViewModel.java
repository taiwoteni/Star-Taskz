package com.theteam.taskz.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<ViewPager2> viewPager = new MutableLiveData<>(null);

    public MutableLiveData<ViewPager2> getViewPager(){
        return viewPager;
    }

    public void setViewPager(ViewPager2 viewPager){
        this.viewPager.setValue(viewPager);
    }

    public void next(){
        viewPager.getValue().setCurrentItem(viewPager.getValue().getCurrentItem()+1, true);
    }
    public void back(){
        viewPager.getValue().setCurrentItem(viewPager.getValue().getCurrentItem()-1, true);
    }
}
