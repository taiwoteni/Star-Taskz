package com.theteam.taskz.domain.entities;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Workspace {
    private String id;

    private String workspaceImage;
    private String creator;
    private ArrayList<String> teamMembers;
    private String workspaceTitle;
    private String workspaceDescription;
    private String createdAt;

    public String workspaceTitle(){
        return workspaceTitle;
    }

    public String workspaceDescription(){
        return workspaceDescription;
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
        return workspaceImage;
    }

    public boolean hasPhoto(){
        return workspaceImage != null;
    }

    public ArrayList<String> teamMembers(){
        return teamMembers;
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
