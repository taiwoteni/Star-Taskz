package com.theteam.taskz.domain.repositories;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UserRepository {
    private Context context;
    private ApiInterface apiInterface;

    public UserRepository(Context context){
        this.context = context;
        apiInterface = new ApiInterface(context);
    }

    public void getUsers(
            final HashMap<String,String> headers,
            final Response.Listener<JSONArray> okResponse,
            final Response.ErrorListener errorResponse
    ){
        apiInterface.getRequestArray(
                "user/get-all",
                headers,
                null,
                okResponse,
                errorResponse
                );
    }

    public void getSingleUser(
            final String id,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        apiInterface.getRequest(
                "user/"+id,
                headers,
                null,
                okResponse,
                errorResponse
        );
    }

    public void updateNames(
            final String id,
            final String firstName,
            final String lastName,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiInterface.postRequest(
                "user/update-names/"+id,
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void updateEmail(
            final String id,
            final String email,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiInterface.postRequest(
                "user/update-email/"+id,
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void updatePassword(
            final String id,
            final String password,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiInterface.postRequest(
                "user/update-password/"+id,
                headers,
                jsonObject,
                okResponse,
                errorResponse
        );
    }

    public void uploadProfilePic(
            final String id,
            final String profilePath,
            final HashMap<String,String> headers,
            final Response.Listener<NetworkResponse> okResponse,
            final Response.ErrorListener errorResponse
    ){
        HashMap<String, MultipartRequest.DataPart> data = new HashMap<>();
        data.put("file", new MultipartRequest.DataPart(profilePath.substring(profilePath.lastIndexOf("/")), getFileDataFromPath(profilePath)));

        apiInterface.multipartPostRequest(
                "user/upload-profileImage/"+id,
                null,
                null,
                data,
                okResponse,
                errorResponse
        );


    }

    public void updateDateOfBirth(
            final String id,
            final Calendar calendar,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){
        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("dateOfBirth", isoFormat.format(calendar.getTime()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        apiInterface.postRequest(
                "user/update-dateOfBirth/"+id,
                headers,
                jsonObject,
                okResponse,
                errorResponse
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







}
