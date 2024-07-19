package com.theteam.taskz.domain.repositories;

import android.content.Context;

import com.android.volley.Response;
import com.theteam.taskz.data.models.TaskModel;

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

    public TaskRepository(Context context){
        this.context = context;
        apiInterface = new ApiInterface(context);
    }

    public void createTask(
            final String id,
            final TaskModel task,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ) throws JSONException {
        final JSONObject jsonObject = task.toJsonObject();

        apiInterface.postRequest(
                "task/add/"+id,
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void updateTaskStatus(
            final String id,
            final String taskId,
            final String status,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", status);
        apiInterface.postRequest(
                "task/update-status/"+id + taskId,
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void updateTaskStartTime(
            final String id,
            final String taskId,
            final Calendar calendar,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ) throws JSONException {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("startedAt", timeFormat.format(calendar.getTime()));
        apiInterface.postRequest(
                "task/update-taskDateTime/"+id + taskId,
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void getOneTask(
            final String id,
            final String taskId,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ) throws JSONException {
        apiInterface.getRequest(
                "task/single-task/"+id + taskId,
                headers,
                null,
                okResponse,
                errorResponse
        );
    }

    public void getAllTasks(
            final String id,
            final HashMap<String,String> headers,
            final Response.Listener<JSONArray> okResponse,
            final Response.ErrorListener errorResponse
    ) throws JSONException {
        apiInterface.getRequestArray(
                "task/all/"+id,
                headers,
                null,
                okResponse,
                errorResponse
        );
    }


}
