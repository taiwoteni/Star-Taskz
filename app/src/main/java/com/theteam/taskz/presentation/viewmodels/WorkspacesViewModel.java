package com.theteam.taskz.presentation.viewmodels;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Response;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.domain.repositories.WorkspaceRepository;

import org.json.JSONObject;

import java.util.ArrayList;

public class WorkspacesViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Workspace>> workspaces = new MutableLiveData<>(new ArrayList<>());
    private WorkspaceRepository workspaceRepository;

    private Context context;

    public MutableLiveData<ArrayList<Workspace>> getWorkspaces(){
        return workspaces;
    }

    public void initializeWithRepository(Context context){
        this.context = context;
        workspaceRepository = new WorkspaceRepository(context);
        setInitialWorkspaces(workspaceRepository.getWorkspaces());
    }

    private void setInitialWorkspaces(ArrayList<Workspace> workspaces){
        this.workspaces.setValue(workspaces);
    }

    public void addWorkspace(Workspace workspace){
        final ArrayList<Workspace> workspaces = this.workspaces.getValue();
        workspaces.add(workspace);
        this.workspaces.setValue(workspaces);
        final SharedPreferences pref = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        pref.edit().putString("workspaces", new Gson().toJson(workspaces, new TypeToken<ArrayList<Workspace>>(){}.getType())).apply();
    }

    public void addWorkspace(Workspace workspace, Context context, Response.Listener<JSONObject> okay, Response.ErrorListener error){
        final ArrayList<Workspace> previousWorkspaces = workspaces.getValue();
        previousWorkspaces.add(workspace);
        workspaces.setValue(previousWorkspaces);
    }

}
