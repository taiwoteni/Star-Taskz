package com.theteam.taskz;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskManager {
    final private ArrayList<TaskModel> tasks = new ArrayList<>();

    private Context context;
    public TaskManager(Context context){
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        ArrayList<HashMap<String, Object>> list = gson.fromJson(jsonString, listType);
        tasks.clear();
        for(final HashMap<String,Object> json : list){
            if(!json.containsKey("id")){
                json.put("id", "#TASK-" + list.indexOf(json));
            }
            tasks.add(new TaskModel(json));
        }

    }
    public void addTask(TaskModel model){
        tasks.add(model);
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        ArrayList<HashMap<String, Object>> list = gson.fromJson(jsonString, listType);
        list.add(model.toJson());
        preferences.edit().putString("tasks", gson.toJson(list, new TypeToken<ArrayList<HashMap<String,Object>>>(){}.getType())).apply();
    }

    public void deleteTask(TaskModel model){
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        ArrayList<HashMap<String, Object>> list = gson.fromJson(jsonString, listType);

        int index = -1;
        for(int i = 0; i<getTasks().size(); i++){
            if(getTasks().get(i).id.equals(model.id)){
                index = i;
                break;
            }
        }

        if(index != -1){
            list.remove(index);
        }
        preferences.edit().putString("tasks", gson.toJson(list, new TypeToken<ArrayList<HashMap<String,Object>>>(){}.getType())).apply();
    }

    public void updateTask(TaskModel model){
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Type listType = new TypeToken<ArrayList<Map<String, Object>>>(){}.getType();
        ArrayList<Map<String, Object>> list = gson.fromJson(jsonString, listType);

        final HashMap<String,Object> maps = model.toJson();

        int index = 0;
        final List<TaskModel> _tasks = getTasks();
        for(final TaskModel _model: _tasks){
            if(Objects.equals(_model.id, model.id)){
                index = _tasks.indexOf(_model);
            }
        }

        for(String key: maps.keySet()) {
            if (list.get(index).containsKey(key)) {
                list.get(index).replace(key, maps.get(key));
            } else {
                list.get(index).put(key, maps.get(key));
            }
        }
        preferences.edit().putString("tasks", gson.toJson(list)).apply();
    }


    public ArrayList<TaskModel> getTasks(){
        return tasks;
    }

    public static TasksListAdapter tasksListAdapter;

    public static CalendarListAdapter calendarListAdapter;
}
