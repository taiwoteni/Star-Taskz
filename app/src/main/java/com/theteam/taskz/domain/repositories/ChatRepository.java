package com.theteam.taskz.domain.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Response;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.entities.Group;
import com.theteam.taskz.domain.entities.Message;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ChatRepository {
    private Context context;
    private ApiInterface apiInterface;
    private SharedPreferences workspacePref;

    private UserModel user;

    public ChatRepository(Context context){
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

    public void sendMessage(
            final Group group,
            final Message message,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
            ){
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("messageContent", message.messageContent());
        JSONObject data = JsonUtils.convertToJsonObject(dataMap);

        apiInterface.postRequest(
                "group/send-groupMessage/"+group.workspaceId() + "/" + group.groupId() + "/" + message.messageId(),
                null,
                data,
                okListener,
                errorListener
        );

    }

    public void getAllWorkspaces(
            final Response.Listener<JSONArray> okListener,
            final Response.ErrorListener errorListener
    ){

        apiInterface.getRequestArray(
                "workSpace/all/"+user.uid(),
                null,
                null,
                okListener,
                errorListener
        );

    }





}
