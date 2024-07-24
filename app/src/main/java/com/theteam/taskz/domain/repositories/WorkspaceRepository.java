package com.theteam.taskz.domain.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.theteam.taskz.data.models.TaskModel;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.entities.Group;
import com.theteam.taskz.domain.entities.Message;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class WorkspaceRepository {
    private Context context;
    private ApiInterface apiInterface;

    private UserModel user;

    public WorkspaceRepository(Context context){
        this.context = context;
        apiInterface = new ApiInterface(context);
        user = new UserModel(context);
    }

    public void createWorkspace(
            final Workspace workspace,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
            ){
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("workSpaceTitle", workspace.workspaceTitle());
        dataMap.put("workSpaceDescription", workspace.workspaceDescription());
        JSONObject data = JsonUtils.convertToJsonObject(dataMap);

        final Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("API_RESPONSE", "Workspace Json:" + JsonUtils.prettyPrint(jsonObject.toString()));

                final Workspace jsonWorkspace = Workspace.fromJson(JsonUtils.convertToHashMap(jsonObject));

                HashMap<String,Object> groupsJson = new HashMap<>();
                groupsJson.put("groupName", workspace.formattedHQName());
                groupsJson.put("groupDescription", "Headquarters of " + workspace.workspaceTitle());

                createGroup(
                        Group.fromJson(groupsJson),
                        jsonWorkspace,

                        // This is the JSONObject from the create group response.
                        // From the response above in the create workspace endpoint,
                        // We override the groups from that response to an array ONLY
                        // containing the group that was added.
                        jsonObject1 -> {
                            Log.v("API_RESPONSE", "Group Json:" + JsonUtils.prettyPrint(jsonObject1.toString()));
                            final JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject1);
                            final JSONObject expandedWorkspaceJson = jsonObject;
                            try {
                                if(expandedWorkspaceJson.optJSONArray("groups")!=null){
                                    expandedWorkspaceJson.remove("groups");
                                }
                                expandedWorkspaceJson.put("groups", jsonArray);
//                                expandedWorkspaceJson.put("workSpaceTitle", workspace.workspaceTitle());
//                                expandedWorkspaceJson.put("workSpaceDescription", workspace.workspaceDescription());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            okListener.onResponse(expandedWorkspaceJson);
                        },
                        errorListener

                        );

            }
        };

        apiInterface.postRequest(
                "workSpace/create-workSpace/"+user.uid(),
                null,
                data,
                response,
                errorListener
        );

    }

    public void editWorkspace(
            final Workspace workspace,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
    ){
        final HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("workspaceTitle", workspace.workspaceTitle());
        dataMap.put("workspaceDescription", workspace.workspaceDescription());
        final JSONObject data = JsonUtils.convertToJsonObject(dataMap);

        final Response.Listener<JSONObject> response = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                apiInterface.putRequest(
                        "workSpace/update-description/"+workspace.workspaceId(),
                        null,
                        data,
                        okListener,
                        errorListener
                );

            }
        };

        apiInterface.putRequest(
                "workSpace/update-Title/"+workspace.workspaceId(),
                null,
                data,
                response,
                errorListener
        );

    }

    public void getAllWorkspaces(
            final Response.Listener<JSONArray> okListener,
            final Response.ErrorListener errorListener
    ){

        final Response.ErrorListener errorListener2 = error -> {
            if (error.networkResponse != null && error.networkResponse.statusCode == 302) {
                try {
                    // Parse the response data directly from the error response
                    String jsonString = new String(error.networkResponse.data, "UTF-8");
                    JSONArray response = new JSONArray(jsonString);
                    // Handle the JSON array response here
                    okListener.onResponse(response);

//                    Log.d("API_RESPONSE", "Redirect Response: " + response.toString());
                } catch (JSONException | java.io.UnsupportedEncodingException e) {
                    e.printStackTrace();
                    errorListener.onErrorResponse(error);
                }
            }
        };

        apiInterface.getRequestArray(
                "workSpace/all/"+user.uid(),
                null,
                null,
                okListener,
                errorListener2
        );

    }

    public void getOneWorkspace(
            final String workspaceId,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
    ){

        apiInterface.getRequest(
                "workSpace/single-workspace/"+workspaceId,
                null,
                null,
                okListener,
                errorListener
        );

    }

    public void deleteWorkspace(
            final Workspace workspace,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
    ){
        final Response.ErrorListener error = volleyError -> {
            if(volleyError.toString().contains("ParseError")){
                okListener.onResponse(new JSONObject());
                return;
            }
            errorListener.onErrorResponse(volleyError);

        };

        apiInterface.deleteRequest(
                "workSpace/delete/"+workspace.workspaceId(),
                null,
                null,
                okListener,
                error
        );

    }

    public void removeMemberFromWorkspace(
            final Workspace workspace,
            final String memberId,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
    ){

        final Response.ErrorListener error = volleyError -> {
            if(volleyError.toString().contains("ParseError")){
                okListener.onResponse(new JSONObject());
                return;
            }
            errorListener.onErrorResponse(volleyError);

        };

        apiInterface.putRequest(
                "workSpace/remove-member/"+workspace.workspaceId() + "/" + memberId,
                null,
                null,
                okListener,
                error
        );

    }

    public void createGroup(
            final Group group,
            final Workspace workspace,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
    ){
        HashMap<String,Object> dataMap = new HashMap<>();
        dataMap.put("groupName", group.groupName());
        dataMap.put("groupDescription", group.groupDescription());
        JSONObject data = JsonUtils.convertToJsonObject(dataMap);

        apiInterface.postRequest(
                "group/create-group/"+ workspace.workspaceId()+ "/" +user.uid() ,
                null,
                data,
                okListener,
                errorListener
        );

    }

    public void uploadWorkspacePhoto(
            final Workspace workspace,
            final String profilePath,
            final Response.Listener<String> okListener,
            final Response.ErrorListener errorListener
    ){
        HashMap<String, MultipartRequest.DataPart> dataParts = new HashMap<>();
        dataParts.put("file", new MultipartRequest.DataPart(profilePath.substring(profilePath.lastIndexOf("/")), getFileDataFromPath(profilePath), "image/jpeg"));
        apiInterface.multipartRequest(
                "workSpace/upload-workspaceImage/"+workspace.workspaceId(),
                Request.Method.POST,
                null,
                null,
                dataParts,
                okListener,
                errorListener

        );
    }

    public byte[] getFileDataFromPath(String filePath) {
        File file = new File(filePath);
        byte[] fileData = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileData;
    }


    public void sendMessages(
            final Group group,
            final Message message,
            final Response.Listener<JSONObject> okListener,
            final Response.ErrorListener errorListener
    ){

        final JSONObject data = new JSONObject();
        try {
            data.put("messageContent",message.messageContent());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        apiInterface.postRequest(
                "group/send-groupMessage/"+group.workspaceId()+"/"+group.groupId()+"/" + user.uid(),
                null,
                data,
                okListener,
                errorListener
        );
    }

    public void addMemberToWorkspace(
            final Workspace workspace,
            final String memberId,
            final Response.Listener<JSONObject> success,
            final Response.ErrorListener errorListener
    ){
        apiInterface.putRequest(
                "workSpace/add-member/"+workspace.workspaceId() + "/" + memberId,
                null,
                null,
                success,
                errorListener
        );
    }
    public void addMemberToGroup(
            final Group group,
            final String memberId,
            final Response.Listener<JSONObject> success,
            final Response.ErrorListener errorListener
    ){
        apiInterface.putRequest(
                "workSpace/add-member/"+group.workspaceId() + "/" + group.groupId() + "/"+ memberId,
                null,
                null,
                success,
                errorListener
        );
    }



}
