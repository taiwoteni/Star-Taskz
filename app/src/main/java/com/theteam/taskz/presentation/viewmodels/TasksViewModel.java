package com.theteam.taskz.presentation.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.theteam.taskz.data.models.TaskManager;
import com.theteam.taskz.data.models.TaskModel;
import com.theteam.taskz.data.repositories.TasksRepository;

import java.util.ArrayList;

public class TasksViewModel extends ViewModel {
    private TasksRepository tasksRepository;

    public TasksViewModel(){
        tasksRepository = new TasksRepository();
    }
    public void loadCachedTasks(Context context){
        final ArrayList<TaskModel> cachedTasks = new TaskManager(context).getTasks();
        tasksRepository.setTasks(cachedTasks);
    }

    public LiveData<ArrayList<TaskModel>> getTasks() {

        return tasksRepository.getTasks();
    }

    public void addTask(TaskModel task) {
        tasksRepository.addTask(task);
    }



}
