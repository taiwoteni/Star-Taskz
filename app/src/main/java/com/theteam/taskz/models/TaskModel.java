package com.theteam.taskz.models;

import com.theteam.taskz.enums.TaskStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TaskModel {
    public Calendar date;
    public String name;
    public String id;

    public int notifId = 0;
    public boolean notifIdExists = false;
    public String category;

    public TaskStatus status;



    public TaskModel(Map<String, Object> json){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(parseStringToTime(json.get("time").toString()));

        date = calendar;
        id = json.get("id").toString();
        name = json.get("name").toString();
        category = json.get("category").toString();
        if(json.containsKey("notifId")){
            notifId = (int) Integer.parseInt(String.valueOf(json.get("notifId").toString()));
            notifIdExists=true;
        }
        if(json.get("status") == null){
            status = TaskStatus.Pending;
        }
        else{
            switch (json.get("status").toString().toLowerCase()){
                case "completed":
                    status = TaskStatus.Completed;
                    break;
                default:
                    status = TaskStatus.Pending;
            }
        }

    }
    private static long parseStringToTime(String timeString) {
        try {
            // Try parsing without handling scientific notation
            return Long.parseLong(timeString);
        } catch (NumberFormatException e) {
            // If parsing fails, try parsing with scientific notation
            try {
                DecimalFormat df = new DecimalFormat("#");
                Number number = df.parse(timeString);
                return number.longValue();
            } catch (ParseException ex) {
                ex.printStackTrace();
                return 0; // or throw an exception as needed
            }
        }
    }

    public void updateStatus(TaskStatus status){
        this.status = status;
    }


    public HashMap<String,Object> toJson(){
        HashMap<String,Object> json = new HashMap<>();
        json.put("id", id);
        json.put("name", name);
        json.put("category", category);
        if(notifIdExists){
            json.put("notifId", String.valueOf(notifId));
        }
        json.put("time", date.getTimeInMillis());
        json.put("status", status.name());

        return json;
    }


    //Used as a direct converter to the body format to be posted on the API
    public JSONObject toJsonObject() throws JSONException {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskName", name);
        jsonObject.put("taskDescription", name);
        jsonObject.put("startDate", dateFormat.format(date));
        jsonObject.put("startTime", isoFormat.format(date));
        jsonObject.put("endDate", dateFormat.format(date));
        jsonObject.put("endTime", isoFormat.format(date));
        jsonObject.put("taskStatus",status.name().toUpperCase());
        jsonObject.put("taskCategory", category.toUpperCase());


        return jsonObject;

    }
}
