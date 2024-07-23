package com.theteam.taskz.domain.entities;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.utils.enums.TaskStatus;

import java.lang.reflect.Type;
import java.util.HashMap;

public class SubTask {
    private String id;
    private String taskName;

    private String status;


    public String subTaskName(){
        return taskName;
    }

    public String subTaskId(){
        return id;
    }

    public TaskStatus subTaskStatus(){
        return status.equalsIgnoreCase("completed")?TaskStatus.Completed:TaskStatus.Pending;
    }

    public boolean completed(){
        return subTaskStatus() == TaskStatus.Completed;
    }

    public static SubTask fromJson(HashMap<String,Object> hashMap){
        Type hashType = new TypeToken<HashMap<String,Object>>(){}.getType();
        Gson gson = new Gson();
        final String json = gson.toJson(hashMap, hashType);
        return gson.fromJson(json, SubTask.class);
    }

    public static SubTask fromJson(String json){
        return new Gson().fromJson(json, SubTask.class);
    }


    public HashMap<String,Object> toJson(){
        String jsonString = new Gson().toJson(this, SubTask.class);
        Type hashMapType = new TypeToken<HashMap<String,Object>>(){}.getType();
        return new Gson().fromJson(jsonString, hashMapType);
    }
}
