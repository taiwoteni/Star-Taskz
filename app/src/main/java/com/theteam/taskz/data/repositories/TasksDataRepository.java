package com.theteam.taskz.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.domain.entities.Workspace;

import java.util.ArrayList;

public class TasksDataRepository {

    private static TasksDataRepository instance;
    private MutableLiveData<ArrayList<Task>> data;

    private TasksDataRepository() {
        data = new MutableLiveData<>(new ArrayList<>());
    }

    public static synchronized TasksDataRepository getInstance() {
        if (instance == null) {
            instance = new TasksDataRepository();
        }
        return instance;
    }

    public void setTasks(final ArrayList<Task> tasks) {
        data.postValue(tasks);
    }

    public MutableLiveData<ArrayList<Task>> getTasks() {
        return data;
    }

    public void addTask(Task task){
        final ArrayList<Task> gotten = data.getValue();
        gotten.add(task);
        setTasks(gotten);

    }
}
