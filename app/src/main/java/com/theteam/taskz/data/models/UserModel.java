package com.theteam.taskz.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.utils.enums.AccountType;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;


public class UserModel {

    private HashMap<String,Object> json;

    private Context context;

    public UserModel(Context context){
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        final String jsonString = preferences.getString("userdata", "{}");
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
        json = gson.fromJson(jsonString, mapType);
//        json.put("accountType", "Business");
//        json.put("jobDescription", "A Front-End Software Engineer");
//        json.put("jobTitle", "Software Developer");



//        For now, we are using a predefined data;
//        final HashMap<String,Object> userJson = new HashMap<>();
//        userJson.put("id", 1);
//        userJson.put("firstName", "John");
//        userJson.put("lastName", "Doe");
//        userJson.put("email", "john.doe@example.com");
//        userJson.put("password", "password");
//        userJson.put("dateOfBirth", "1990-01-01");
//        userJson.put("accountType", "Business");
//        userJson.put("jobDescription", "A Front-End Software Engineer");
//        userJson.put("jobTitle", "Software Developer");
//        userJson.put("authToken", "1234567890");
//        userJson.put("tokenExpiration", "2023-01-01T00:00:00");
//        userJson.put("sync", false);
//        json = userJson;
    }

    public static void saveUserData(HashMap<String,Object> map, Context context){
        SharedPreferences preferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();

        preferences.edit().putString("userdata",gson.toJson(map, mapType)).apply();
    }
    public HashMap<String,Object> toJson(){
        return json;
    }
    public static void clearUserData(Context context){
        SharedPreferences preferences = context.getSharedPreferences("userData", Context.MODE_PRIVATE);

        preferences.edit().remove("userdata").apply();
    }

    public void setNeedsToSync(boolean sync){
        final HashMap<String,Object> userJson = (HashMap<String, Object>) json.clone();

        userJson.replace("sync", sync);
        saveUserData(userJson, context);
    }

    public boolean needsSync(){
        return (boolean) json.get("sync");

    }

    public boolean isExists(){
        return !json.isEmpty();
    }

    public String uid(){
        return json.get("id").toString();
    }
    public String authToken(){
        return json.get("authToken").toString();
    }
    public AccountType accountType(){
        return AccountType.valueOf(json.get("accountType").toString().trim());
    }
    public String firstName(){
        return json.get("firstName").toString();
    }
    public String lastName(){
        return json.get("lastName") == null? "": json.get("lastName").toString();
    }
    public String fullName(){
        return firstName() + (lastName().trim().isEmpty()?"":(" " + lastName()));
    }
    public String jobTitle(){
        return json.get("jobTitle").toString();
    }
    public String jobDescription(){
        return json.get("jobDescription").toString();
    }
    public String email(){
        return json.get("email").toString();
    }
    public String password(){
        return json.get("password").toString();
    }

    public String profile(){
        String src = json.get("profilePicture") == null? json.get("profile").toString():json.get("profilePicture").toString();
        // Becos glide does'nt accept http
        final String httpsString = src.startsWith("https://")? src: src.replace("http://", "https://");
        return httpsString;
    }

    public boolean isGoogleAccount(){
        return  password().trim().equals("sTaR_TaSkZ@30_May@" + email());
    }

    public boolean hasProfile(){

        return json.get("profile") != null || json.get("profilePicture") != null;
    }

    public boolean hasBirthday(){
        return json.get("dateOfBirth") != null;
    }


    public Calendar birthday(){
        String time = json.get("dateOfBirth").toString();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(Objects.requireNonNull(sdf.parse(time)));
            calendar.set(Calendar.YEAR, c.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, c.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        } catch (Exception e) {
            Log.e("AI", Objects.requireNonNull(e.getMessage()));
        }
        return calendar;
    }
}
