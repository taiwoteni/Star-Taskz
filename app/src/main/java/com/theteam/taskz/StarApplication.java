package com.theteam.taskz;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.data.repositories.TasksDataRepository;
import com.theteam.taskz.data.repositories.TasksPreferences;
import com.theteam.taskz.data.repositories.WorkspaceDataRepository;
import com.theteam.taskz.data.repositories.WorkspacePreferences;
import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.TaskRepository;
import com.theteam.taskz.domain.repositories.WorkspaceRepository;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class StarApplication extends Application {
    private Handler handler;
    private Runnable runnable;
    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        startRepeatingTask();
    }

    private void startRepeatingTask() {
        runnable = new Runnable() {
            @Override
            public void run() {
                fetchTasksFromApi();
                fetchWorkspacesFromApi();
                handler.postDelayed(this, 3000); // 3 seconds
            }
        };
        handler.post(runnable);
    }

    public void fetchWorkspacesFromApi(){
//        Log.v("API_RESPONSE", "Getting Data");
        final UserModel user = new UserModel(getApplicationContext());
//        Log.v("API_RESPONSE", "User exists:" + user.isExists());

        if(!user.isExists()){
            return;
        }

        new WorkspaceRepository(getApplicationContext())
                .getAllWorkspaces(
                        jsonArray -> {
//                            Log.v("API_RESPONSE", JsonUtils.prettyPrint(jsonArray.toString()));

                            final WorkspacePreferences workspacePreferences = new WorkspacePreferences(getApplicationContext());
                            try {
                                final JSONArray local = new JSONArray(workspacePreferences.getCachedWorkspacesSrc());

                                // We only want to update if values have changed
                                if(jsonArray.toString().equals(local.toString())){
                                    return;
                                }
                                final ArrayList<Workspace> workspaces = new Gson().fromJson(jsonArray.toString(), new TypeToken<ArrayList<Workspace>>(){}.getType());
                                WorkspaceDataRepository.getInstance().setWorkspaces(workspaces);
                                workspacePreferences.saveWorkspaces(workspaces);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        },
                        error -> {
//                            Log.e("API_RESPONSE", "Error in service: " + error.toString());


                        }
                );
    }
    public void fetchTasksFromApi(){
//        Log.v("API_RESPONSE", "Getting Data");
        final UserModel user = new UserModel(getApplicationContext());
//        Log.v("API_RESPONSE", "User exists:" + user.isExists());

        if(!user.isExists()){
            return;
        }

        new TaskRepository(getApplicationContext())
                .getAllTasks(
                        null,
                        jsonArray -> {
//                            Log.v("API_RESPONSE", JsonUtils.prettyPrint(jsonArray.toString()));

                            final TasksPreferences taskPreferences = new TasksPreferences(getApplicationContext());
                            try {
                                final JSONArray local = new JSONArray(taskPreferences.getCachedTasksSrc());

                                // We only want to update if values have changed
                                if(jsonArray.toString().equals(local.toString())){
                                    return;
                                }

//                                Log.v("API_RESPONSE", "Data changed");
                                final ArrayList<Task> tasks = new Gson().fromJson(jsonArray.toString(), new TypeToken<ArrayList<Task>>(){}.getType());

                                TasksDataRepository.getInstance().setTasks(tasks);
                                taskPreferences.saveTasks(tasks);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        },
                        error -> {
                            Log.e("API_RESPONSE", "Error in task service: " + error.toString());


                        }
                );
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        stopRepeatingTask();
    }

    private void stopRepeatingTask() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

}
