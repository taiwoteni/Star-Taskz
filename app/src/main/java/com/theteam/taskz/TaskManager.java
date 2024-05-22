package com.theteam.taskz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskManager {
    final private ArrayList<TaskModel> tasks = new ArrayList<>();

    private RequestQueue requestQueue;

    public static String END_POINT = "https://startaskzbackend-production.up.railway.app/";

    private Context context;

    private UserModel userModel;
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

        requestQueue = Volley.newRequestQueue(context);

    }
    public void addTask(TaskModel model){
        userModel = new UserModel(context);
        tasks.add(model);
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("tasks", "[]");
        Type listType = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
        ArrayList<HashMap<String, Object>> list = gson.fromJson(jsonString, listType);
        list.add(model.toJson());
        preferences.edit().putString("tasks", gson.toJson(list, new TypeToken<ArrayList<HashMap<String,Object>>>(){}.getType())).apply();

        refresh(model);


//        StringRequest createTaskRequest = new StringRequest(
//                Request.Method.POST,
//                END_POINT + "user/create-task/" + userModel.uid(),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String string) {
//                        Log.v("API_REP", string);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.e("API_REP", volleyError.getMessage());
//                    }
//                }
//                ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("param1", "value1");
//                params.put("param2", "value2");
//                // Add more parameters as needed
//                return params;
//            }
//        };
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

        refresh(model);
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

        refresh(model);
    }

    public ArrayList<TaskModel> getTasks(){
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
        return tasks;
    }

    private void refresh(TaskModel model){
        if(StateHolder.taskPageFragments!= null){
            //Inorder to automatically refresh the Page that needs this update,
            for(TasksPageFragment fragment: StateHolder.taskPageFragments){
                if(fragment.date.get(Calendar.MONDAY) == model.date.get(Calendar.MONTH) && fragment.date.get(Calendar.DAY_OF_MONTH) == model.date.get(Calendar.DAY_OF_MONTH)){
                    //We refresh
                    if(fragment.requireActivity() != null){
                        fragment.instantiateTasks();
                    }
                }
            }
        }
        if(StateHolder.calendarPageFragments != null){
            for(CalendarPageFragment fragment: StateHolder.calendarPageFragments){
                if(fragment.date.get(Calendar.MONDAY) == model.date.get(Calendar.MONTH) && fragment.date.get(Calendar.DAY_OF_MONTH) == model.date.get(Calendar.DAY_OF_MONTH)){
                    //We refresh
                    if(fragment.requireActivity() != null){
                        fragment.instantiateTasks();
                    }
                }
            }

        }
    }

}
