package com.theteam.taskz.data.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.theteam.taskz.data.models.TaskModel;

import java.util.ArrayList;

public class TasksRepository {
    private MutableLiveData<ArrayList<TaskModel>> tasks = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<ArrayList<TaskModel>> getTasks(){
        return tasks;
    }

    public void setTasks(ArrayList<TaskModel> tasks){
        this.tasks.setValue(tasks);
    }

    public void addTask(@NonNull TaskModel task){
        final ArrayList<TaskModel> currentTasks = tasks.getValue();
        currentTasks.add(task);
        tasks.setValue(currentTasks);

    }
}
