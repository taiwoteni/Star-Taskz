package com.theteam.taskz.domain.entities;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class Workspace {
    private String workspaceTitle;
    private String workspaceDescription;
    private List<Group> groups;
    private List<Member> members;

    public String workspaceTitle(){
        return workspaceTitle;
    }

    public String workspaceDescription(){
        return workspaceDescription;
    }


    public static Workspace fromJson(String json){
        return new Gson().fromJson(json, Workspace.class);
    }

    public HashMap<String,Object> toJson(){
        String jsonString = new Gson().toJson(this, Workspace.class);
        Type hashMapType = new TypeToken<HashMap<String,Object>>(){}.getType();
        return new Gson().fromJson(jsonString, hashMapType);
    }
}
