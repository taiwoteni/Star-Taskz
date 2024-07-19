package com.theteam.taskz.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.theteam.taskz.presentation.views.SplashRefreshLayout;

public class SplashViewModel extends ViewModel {
    private MutableLiveData<SplashRefreshLayout> homeSplashLayout = new MutableLiveData<>(null);

    public void setHomeSplashLayout(SplashRefreshLayout splashLayout){
        homeSplashLayout.setValue(splashLayout);
    }

    public MutableLiveData<SplashRefreshLayout> getHomeSplashLayout(){
        return homeSplashLayout;
    }
}
