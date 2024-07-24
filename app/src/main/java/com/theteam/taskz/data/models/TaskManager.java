package com.theteam.taskz.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.data.repositories.TasksPreferences;
import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.utils.enums.TaskStatus;
import com.theteam.taskz.utils.others.AlarmManager;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskManager {
    private Context context;

    private UserModel userModel;
    private TasksPreferences tasksPreferences;

    public TaskManager(Context context){
        this.context = context;
       tasksPreferences = new TasksPreferences(context);
       userModel = new UserModel(context);

    }

    public void addTask(Task task, boolean speak){

        if(Calendar.getInstance().getTime().before(task.startTime().getTime())){
            if(!task.completed()){
                final AlarmManager taskReminder = new AlarmManager(context.getApplicationContext(), context);
                taskReminder.setAlarm(task, speak);
            }

        }
    }

    public void createTaskOffline(Task task, boolean speak){

        final String string = String.valueOf(tasksPreferences.generateOfflineId());
        final HashMap<String,Object> hashMap = task.toJson();
        hashMap.put("alarmId", string);
        if(!hashMap.containsKey("id")){
            hashMap.put("id",string);
        }

        hashMap.put("status", TaskStatus.Pending.name());

        final Task generatedTask = Task.fromJson(hashMap);
        addTask(generatedTask, speak);

    }

    public void updateTaskOffline(){

    }

}
