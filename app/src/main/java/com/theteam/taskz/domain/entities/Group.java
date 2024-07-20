package com.theteam.taskz.domain.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Group {
    private String id;
    private String workspace;
    private String groupName;
    private String groupDescription;
    private String groupImage;
    private String createdAt;
    private ArrayList<String> members;

    public String workspaceId(){
        return workspace;
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
        return groupImage!=null;
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

}
