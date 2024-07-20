package com.theteam.taskz.data.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class GithubAccount {
    public String username,name,email,link,bio,token,pic;
    public int followers, following, repos;

    public Calendar createdAt;

    private HashMap<String,Object> json;

    private GithubAccount(HashMap<String,Object> json){
        // This isn't in the initial json data returned
        // In Github's endpoint, But it is manually added
        if(json.get("token") != null){
            token = json.get("token").toString();
        }
        username = json.get("login").toString();
        name = json.get("name").toString();
        if(json.get("email") != null){
            email = json.get("email").toString();
        }
        if(json.get("bio") != null){
            bio = json.get("bio").toString();
        }
        link = json.get("url").toString();
        pic = json.get("avatar_url").toString();
        followers = (int) Double.parseDouble(json.get("followers").toString());
        following = (int) Double.parseDouble(json.get("following").toString());
        repos = (int) Double.parseDouble(json.get("public_repos").toString());

        Calendar created = Calendar.getInstance();
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.getDefault());

        try {
            created.setTime(format.parse(json.get("created_at").toString()));
            createdAt=created;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        this.json = json;
    }

    public static GithubAccount fromJson(HashMap<String,Object> json){

        return new GithubAccount(json);
    }

    public HashMap<String,Object> toJson(){
        return json;
    }
}
