package com.theteam.taskz.data.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.domain.entities.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GithubIssue extends Task{

    public static Task fromRawJson(HashMap<String,Object> json){
        HashMap<String,Object> issueJson = new HashMap<>();

        issueJson.put("id", json.get("id"));
        issueJson.put("globalId", json.get("id"));
        issueJson.put("taskName", json.get("title").toString());
        issueJson.put("taskStatus", json.get("state").toString().equalsIgnoreCase("open")?"pending":"completed");
        issueJson.put("startedAt", json.get("created_at"));
        issueJson.put("endedAt", json.get("closed_at"));
        issueJson.put("taskDescription",json.get("body"));
        issueJson.put("taskCategory","uncategorized");

        final List<String> categories = new ArrayList<String>();
        categories.add("work");
        categories.add("study");
        categories.add("holiday");
        categories.add("personal");

        if(json.get("labels") != null){
            final ArrayList<Map<String,Object>> labels = new Gson().fromJson(json.get("labels").toString(), new TypeToken<ArrayList<Map<String,Object>>>(){}.getType());
            for(final Map<String,Object> label : labels){
                if(categories.add(label.get("name").toString().toLowerCase())){
                    issueJson.replace("taskCategory",label);
                    break;
                }
            }
        }
        return Task.fromJson(issueJson);
    }


}
