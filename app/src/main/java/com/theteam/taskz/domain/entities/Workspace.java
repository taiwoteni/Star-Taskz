package com.theteam.taskz.domain.entities;

import android.content.Context;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.data.models.UserModel;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Workspace {
    private String id;

    private String workSpaceImage;
    private String creator;
    private ArrayList<String> teamMembers;
    private String workSpaceTitle;
    private String workSpaceDescription;
    private String createdAt;
    private ArrayList<Group> groups;

    public String workspaceTitle(){
        return workSpaceTitle;
    }

    public String workspaceDescription(){
        return workSpaceDescription;
    }

    public String workspaceId(){
        return id;
    }

    public String admin(){
        return creator;
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

    public String workspacePhoto(){
        return workSpaceImage;
    }

    public boolean hasPhoto(){
        return workSpaceImage != null;
    }

    public ArrayList<String> teamMembers(){
        return teamMembers;
    }

    public ArrayList<Group> groups(){
        return groups;
    }

    public boolean isCreator(Context context){
        return new UserModel(context).uid().equals(creator);
    }

    public static Workspace fromJson(String json){
        return new Gson().fromJson(json, Workspace.class);
    }
    public static Workspace fromJson(HashMap<String,Object> hashMap){
        Type hashType = new TypeToken<HashMap<String,Object>>(){}.getType();
        Gson gson = new Gson();
        final String json = gson.toJson(hashMap, hashType);
        return gson.fromJson(json, Workspace.class);
    }

    public HashMap<String,Object> toJson(){
        String jsonString = new Gson().toJson(this, Workspace.class);
        Type hashMapType = new TypeToken<HashMap<String,Object>>(){}.getType();
        return new Gson().fromJson(jsonString, hashMapType);
    }

    public String formattedHQName(){
        final String title = workSpaceTitle.trim();

        if(title.toLowerCase().endsWith("workspace")){
            return title.substring(0, title.toLowerCase().lastIndexOf("workspace")) + " HQ";
        }
        if(title.toLowerCase().endsWith("team")){
            return title.substring(0, title.toLowerCase().lastIndexOf("team")) + " HQ";
        }
        return title + " HQ";
    }

    public void addGroup(Group group){
        groups.add(group);
    }
    public void setGroups(ArrayList<Group> groups){
        this.groups = groups;
    }



}
