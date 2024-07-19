package com.theteam.taskz.domain.repositories;

import android.content.Context;

import com.android.volley.Response;
import com.theteam.taskz.data.models.AuthenticationDataHolder;
import com.theteam.taskz.data.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AuthenticationRepository {
    private Context context;
    private ApiInterface apiInterface;

    public AuthenticationRepository(Context context){
        this.context = context;
        apiInterface = new ApiInterface(context);
    }

    public void loginUser(
            final String email,
            final String password,
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){

        final JSONObject data = new JSONObject();
        try {
            data.put("email", email);
            data.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        apiInterface.postRequest(
                "user/login",
                headers,
                data,
                okResponse,
                errorResponse
                );
    }

    public void registerUser(
            final HashMap<String,String> headers,
            final Response.Listener<JSONObject> okResponse,
            final Response.ErrorListener errorResponse
    ){

        final JSONObject data = new JSONObject();
        try {
            data.put("firstName", AuthenticationDataHolder.firstName);
            data.put("lastName", AuthenticationDataHolder.lastName);
            data.put("email", AuthenticationDataHolder.email);
            data.put("password", AuthenticationDataHolder.password);
            data.put("dateOfBirth", AuthenticationDataHolder.dob);
            data.put("jobTitle", AuthenticationDataHolder.jobTitle);
            data.put("jobDescription", AuthenticationDataHolder.jobDescription);
            data.put("accountType", AuthenticationDataHolder.selecAccountType.name());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        apiInterface.postRequest(
                "user/register",
                headers,
                data,
                okResponse,
                errorResponse
        );
    }


}
