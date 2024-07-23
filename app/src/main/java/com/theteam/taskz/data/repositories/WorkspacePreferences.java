package com.theteam.taskz.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.domain.entities.Workspace;

import java.util.ArrayList;

public class WorkspacePreferences {

    private Context context;
    private SharedPreferences sharedPreferences;
    public WorkspacePreferences(Context context){
        this.context = context;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("userData", Context.MODE_PRIVATE);
    }

    public ArrayList<Workspace> getCachedWorkspaces(){
        final String src = sharedPreferences.getString("workSpaces", null);
        if (src == null){
            return new ArrayList<>();
        }

       return new Gson().fromJson(src, new TypeToken<ArrayList<Workspace>>(){}.getType());
    }

    public String getCachedWorkspacesSrc(){
        final String src = sharedPreferences.getString("workSpaces", null);
        if (src == null){
            return "[]";
        }

        return src;
    }



    public void saveWorkspaces(final ArrayList<Workspace> workspaces){
        sharedPreferences.edit().putString("workSpaces",new Gson().toJson(workspaces, new TypeToken<ArrayList<Workspace>>(){}.getType())).apply();
    }
}
