package com.theteam.taskz.domain.entities;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Group {
    private String id;
    private String workSpace;
    private String groupName;
    private String groupDescription;
    private String groupImageUrl;
    private String createdAt;
    private ArrayList<String> members;

    private ArrayList<Message> messages;


    public String workspaceId(){
        return workSpace;
    }
    public String groupId(){
        return id;
    }
    public String groupName(){
        return groupName;
    }
    public String groupDescription(){
        return groupDescription;
    }
    public boolean hasDescription(){
        return groupDescription!=null;
    }
    public boolean hasPhoto(){
        return groupImageUrl !=null;
    }
    public String groupPhoto(){
        final String src = groupImageUrl;
        final String httpsString = src.startsWith("https://")? src: src.replace("http://", "https://");
        return httpsString;
    }
    public ArrayList<String> members(){
        return members;
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

    public ArrayList<Message> getMessages(){
        return messages;
    }

    public boolean isHeadQuarter(){
        return groupName.endsWith("HQ");
    }

    public static Group fromJson(HashMap<String,Object> hash){
        return new Gson().fromJson(new Gson().toJson(hash, new TypeToken<HashMap<String, Object>>(){}.getType()), Group.class);
    }

    public static Group fromJson(String src){
        return new Gson().fromJson(src, Group.class);
    }

    public HashMap<String,Object> toJson(){
        String jsonString = new Gson().toJson(this, Group.class);
        Type hashMapType = new TypeToken<HashMap<String,Object>>(){}.getType();
        return new Gson().fromJson(jsonString, hashMapType);
    }


}
