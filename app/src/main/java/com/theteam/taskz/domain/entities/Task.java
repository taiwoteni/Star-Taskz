package com.theteam.taskz.domain.entities;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.utils.enums.TaskStatus;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Task {
    private String id;
    private String globalId;
    private String taskName;
    private ArrayList<String> assignedTo;
    private String createdAt;
    private String startedAt;
    private String endedAt;

    private boolean needsSync;

    private ArrayList<SubTask> steps;

    private String status;

    public static Task fromJson(HashMap<String,Object> hashMap){
        Type hashType = new TypeToken<HashMap<String,Object>>(){}.getType();
        Gson gson = new Gson();
        final String json = gson.toJson(hashMap, hashType);
        return gson.fromJson(json, Task.class);
    }

    public static Task fromJson(String json){
        return new Gson().fromJson(json, Task.class);
    }


    public HashMap<String,Object> toJson(){
        String jsonString = new Gson().toJson(this, Task.class);
        Type hashMapType = new TypeToken<HashMap<String,Object>>(){}.getType();
        return new Gson().fromJson(jsonString, hashMapType);
    }

    public String taskName(){
        return taskName;
    }

    public String taskId(){
        return id;
    }

    public int taskLocalId(){
        return (int)(Double.parseDouble(globalId));
    }

    public boolean taskNeedsSync(){
        return needsSync;
    }

    public ArrayList<SubTask> taskSteps(){
        return steps;
    }

    public TaskStatus taskStatus(){
        return status.equalsIgnoreCase("completed")?TaskStatus.Completed:TaskStatus.Pending;
    }

    public boolean completed(){
        return taskStatus() == TaskStatus.Completed;
    }

    public int taskProgress(){
        if(steps.isEmpty()){
            return taskStatus()==TaskStatus.Completed?100:0;
        }

        int max = steps.size();

        int completedCount = 0;

        for(int i = 0; i<max; i++){
            if(steps.get(i).completed()){
                completedCount++;
            }
        }


        return (completedCount/max)*100;
    }

    public Calendar createdTime(){
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());

        try {
            calendar.setTime(format.parse(createdAt));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }

    public Calendar startTime(){
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());

        try {
            calendar.setTime(format.parse(startedAt));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }

    public boolean hasDeadline(){
        return endedAt!=null;
    }

    public Calendar dueTime(){
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());

        try {
            calendar.setTime(format.parse(endedAt));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }


}






