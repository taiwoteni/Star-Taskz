package com.theteam.taskz.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.domain.entities.Workspace;

import java.util.ArrayList;
import java.util.Random;

public class TasksPreferences {

    private Context context;
    private SharedPreferences sharedPreferences;
    public TasksPreferences(Context context){
        this.context = context;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("userData", Context.MODE_PRIVATE);
    }

    public ArrayList<Task> getCachedTasks(){
        final String src = sharedPreferences.getString("tasks", null);
        if (src == null){
            return new ArrayList<>();
        }

       return new Gson().fromJson(src, new TypeToken<ArrayList<Task>>(){}.getType());
    }

    public String getCachedTasksSrc(){
        final String src = sharedPreferences.getString("tasks", null);
        if (src == null){
            return "[]";
        }

        return src;
    }



    public void saveTasks(final ArrayList<Task> tasks) {
        sharedPreferences.edit().putString("tasks",new Gson().toJson(tasks, new TypeToken<ArrayList<Task>>(){}.getType())).apply();
    }

    public int generateOfflineId(){
        final ArrayList<Task> tasks = new ArrayList<>();

        for (Task task : getCachedTasks()){
            if(task.taskScheduledOffline()){
                tasks.add(task);
            }
        }

        int id = 0;
        on:
        while (true){
            int i = g();
            for (final Task task: tasks){
                if (task.taskLocalId() == i){
                    continue  on;
                }
            }
            id = i;
            break on;
        }

        return id;

    }

    private int g(){
        final int first = new Random().nextInt(1000);
        final int second = new Random().nextInt(first);
        final int third = new Random().nextInt(first+second);

        final int fourth = (first+second+third);

        return fourth;
    }
}
