package com.theteam.taskz.presentation.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.theteam.taskz.presentation.adapters.TaskDateListAdapter;

import java.util.Calendar;

public class TaskDatesViewModel extends ViewModel {
    private MutableLiveData<TaskDateListAdapter.TaskDateViewHolder> selectedView = new MutableLiveData<>();
    private MutableLiveData<TaskDateListAdapter.TaskDateViewHolder> previousView = new MutableLiveData<>();
    private MutableLiveData<TaskDateListAdapter.TaskDateViewHolder> todayView = new MutableLiveData<>();
    private MutableLiveData<Calendar> selectedDate = new MutableLiveData<>(Calendar.getInstance());
    private MutableLiveData<Boolean> viewPagerScroll = new MutableLiveData<>(false);



    public MutableLiveData<TaskDateListAdapter.TaskDateViewHolder> getSelectedView(){
        return selectedView;
    }

    public void setSelectedView(TaskDateListAdapter.TaskDateViewHolder holder){
        selectedView.setValue(holder);
    }

    public MutableLiveData<TaskDateListAdapter.TaskDateViewHolder> getPreviousView(){
        return previousView;
    }

    public void setPreviousView (TaskDateListAdapter.TaskDateViewHolder holder){
        previousView.setValue(holder);
    }

    public MutableLiveData<TaskDateListAdapter.TaskDateViewHolder> getTodayView(){
        return todayView;
    }
    public void setTodayView(TaskDateListAdapter.TaskDateViewHolder holder){
        todayView.setValue(holder);
    }

    public MutableLiveData<Calendar> getSelectedDate(){
        return selectedDate;
    }

    public MutableLiveData<Boolean> getViewPagerScroll(){
        return viewPagerScroll;
    }



    public void setSelectedDate(Calendar date){
        selectedDate.setValue(date);
    }
    public void setSelectedDateFromViewPager(Calendar date){
        viewPagerScroll.setValue(true);
        selectedDate.setValue(date);

    }
    public void forgetViewPagerScroll(){
        viewPagerScroll.setValue(false);
    }



}
