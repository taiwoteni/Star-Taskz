package com.theteam.taskz.domain.repositories;

import android.content.Context;

import com.android.volley.Response;
import com.theteam.taskz.data.models.TaskModel;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.domain.entities.Workspace;
import com.theteam.taskz.utils.enums.TaskStatus;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class TaskRepository {
    private Context context;
    private ApiInterface apiInterface;

    private UserModel user;

    public TaskRepository(Context context){
        this.context = context;
        apiInterface = new ApiInterface(context);
        user = new UserModel(context);
    }

    public void createTask(
            final Task task,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        final JSONObject jsonObject = JsonUtils.convertToJsonObject(task.toJson());

        apiInterface.postRequest(
                "task/add/"+user.uid(),
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }
    public void createTask(
            Workspace workspace,
            final Task task,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        final JSONObject jsonObject = JsonUtils.convertToJsonObject(task.toJson());

        apiInterface.postRequest(
                "create-task"+workspace.workspaceId() + "/" +user.uid(),
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void updateTaskStatus(
            final String taskId,
            final TaskStatus status,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", status.name().toLowerCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        apiInterface.postRequest(
                "task/update-status/"+user.uid() + "/" + taskId,
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void updateTaskStartTime(
            final String taskId,
            final Calendar calendar,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("startedAt", timeFormat.format(calendar.getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        apiInterface.postRequest(
                "task/update-taskDateTime/"+user.uid() + "/" + taskId,
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void getOneTask(
            final String taskId,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        apiInterface.getRequest(
                "task/single-task/"+user.uid() + "/" + taskId,
                headers,
                null,
                okResponse,
                errorResponse
        );
    }

    public void getAllTasks(
            final HashMap<String,String> headers,
            final Response.Listener<JSONArray> okResponse,
            final Response.ErrorListener errorResponse
    ){

        final Response.ErrorListener errorListener2 = error -> {
            if (error.networkResponse != null && error.networkResponse.statusCode == 302) {
                try {
                    // Parse the response data directly from the error response
                    String jsonString = new String(error.networkResponse.data, "UTF-8");
                    JSONArray response = new JSONArray(jsonString);
                    // Handle the JSON array response here
                    okResponse.onResponse(response);

//                    Log.d("API_RESPONSE", "Redirect Response: " + response.toString());
                } catch (JSONException | java.io.UnsupportedEncodingException e) {
                    e.printStackTrace();
                    errorResponse.onErrorResponse(error);
                }
            }
        };

        apiInterface.getRequestArray(
                "task/all/"+user.uid(),
                headers,
                null,
                okResponse,
                errorListener2
        );
    }


}
