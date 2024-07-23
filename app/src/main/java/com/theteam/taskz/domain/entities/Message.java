package com.theteam.taskz.domain.entities;

import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Message {

    private String id;
    private String senderId;
    private String messageContent;
    private ArrayList<String> readBy;
    private String dateTime;
    private String profile;


    public static Message fromJson(HashMap<String,Object> json){
        Type hashType = new TypeToken<HashMap<String,Object>>(){}.getType();
        final Gson gson = new Gson();
        final String src = gson.toJson(json, hashType);
        return gson.fromJson(src, Message.class);
    }

    public static Message fromJson(String src){
        final Gson gson = new Gson();
        return gson.fromJson(src, Message.class);
    }

    public HashMap<String,Object> toJson(){
        String jsonString = new Gson().toJson(this, Message.class);
        Type hashMapType = new TypeToken<HashMap<String,Object>>(){}.getType();
        return new Gson().fromJson(jsonString, hashMapType);
    }

    public String messageId(){
        return id;
    }

    public String senderId() {
        return senderId;
    }

    public String messageContent() {
        return messageContent;
    }

    public ArrayList<String> readReceipts() {
        return readBy;
    }

    public Calendar messageTime() {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());

        try {
            calendar.setTime(format.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar;
    }

    public boolean hasProfile(){
        return profile!=null;
    }

    public String senderProfile() {
        return profile;
    }
}
