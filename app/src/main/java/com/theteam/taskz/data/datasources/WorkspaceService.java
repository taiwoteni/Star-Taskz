package com.theteam.taskz.data.datasources;

import android.app.IntentService;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.data.repositories.WorkspaceDataRepository;
import com.theteam.taskz.data.repositories.WorkspacePreferences;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.WorkspaceRepository;
import com.theteam.taskz.presentation.viewmodels.WorkspacesViewModel;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorkspaceService extends Worker {


    public WorkspaceService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public void fetchWorkspacesFromApi(){
        Log.v("API_RESPONSE", "Getting Data");
        final UserModel user = new UserModel(getApplicationContext());
        Log.v("API_RESPONSE", "User exists:" + user.isExists());

        if(!user.isExists()){
            return;
        }

        new WorkspaceRepository(getApplicationContext())
                .getAllWorkspaces(
                        jsonArray -> {
                            Log.v("API_RESPONSE", JsonUtils.prettyPrint(jsonArray.toString()));

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
                            Log.e("API_RESPONSE", "Error in service: " + error.toString());


                        }
                );
    }

    @NonNull
    @Override
    public Result doWork() {
        fetchWorkspacesFromApi();
        return Result.success();
    }
}
