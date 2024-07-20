package com.theteam.taskz.domain.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Response;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.theteam.taskz.data.models.TaskModel;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class WorkspaceRepository {
    private Context context;
    private ApiInterface apiInterface;
    private SharedPreferences workspacePref;

    private UserModel user;

    public WorkspaceRepository(Context context){
        this.context = context;
        apiInterface = new ApiInterface(context);
        workspacePref = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
        user = new UserModel(context);
    }

    public ArrayList<Workspace> getWorkspaces(){
        final String workspaces = workspacePref.getString("workspaces", null);
        if(workspaces == null){
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        return gson.fromJson(workspaces, new TypeToken<ArrayList<Workspace>>(){}.getType());
    }

    public void createWorkspace(
            final Workspace workspace,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
            ){
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("workspaceTitle", workspace.workspaceTitle());
        dataMap.put("workspaceDescription", workspace.workspaceDescription());
        JSONObject data = JsonUtils.convertToJsonObject(dataMap);

        apiInterface.postRequest(
                "create-workSpace/"+user.uid(),
                null,
                data,
                okListener,
                errorListener
        );

    }


}
