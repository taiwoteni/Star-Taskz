package com.theteam.taskz.domain.repositories;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class ApiInterface {
    private final String baseUrl = "https://star-taskz-backend.onrender.com/star-taskz/api/";
    private Context context;
    private RequestQueue queue;

    private static final int MY_DEFAULT_TIMEOUT = 20000; // 20 seconds
    private static final int MY_MAX_RETRIES = 3;        // Number of retry attempts
    private static final float MY_BACKOFF_MULTIPLIER = 1.0f;

    public ApiInterface(Context application_context){
        this.context = application_context;
        queue = Volley.newRequestQueue(application_context);
    }

    public void postRequest(
            String path,
            HashMap<String,String> headers,
            JSONObject data,
            Response.Listener<JSONObject> onSuccess,
            Response.ErrorListener onFailure
    ){
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                path.startsWith("http")?path:baseUrl+path,
                data,
                onSuccess,
                onFailure){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // In case no data is passed in as a header explicitly
                final HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type","application/json");
                if(headers != null){
                    header.putAll(headers);
                }
                return header;
            }
        };
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                MY_MAX_RETRIES,
                MY_BACKOFF_MULTIPLIER));
        queue.add(objectRequest);
    }
    public void putRequest(
            String path,
            HashMap<String,String> headers,
            JSONObject data,
            Response.Listener<JSONObject> onSuccess,
            Response.ErrorListener onFailure
    ){
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                baseUrl+path,
                data,
                onSuccess,
                onFailure){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // In case no data is passed in as a header explicitly
                final HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type","application/json");
                if(headers != null){
                    header.putAll(headers);
                }
                return header;
            }
        };
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                MY_MAX_RETRIES,
                MY_BACKOFF_MULTIPLIER));
        queue.add(objectRequest);
    }

    public void multipartRequest(
            String path,
            int method,
            HashMap<String,String> headers,
            HashMap<String,String> data,
            Map<String, MultipartRequest.DataPart> dataParts,
            Response.Listener<String> onSuccess,
            Response.ErrorListener onFailure
    ){

        MultipartRequest multipartRequest = new MultipartRequest(
                baseUrl+path,
                headers,
                method,
                data,
                dataParts,
                onSuccess,
                onFailure);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                MY_MAX_RETRIES,
                MY_BACKOFF_MULTIPLIER));
        queue.add(multipartRequest);
    }

    public void getRequestArray(
            String path,
            HashMap<String,String> headers,
            JSONArray data,
            Response.Listener<JSONArray> onSuccess,
            Response.ErrorListener onFailure
    ){
        JsonArrayRequest objectRequest = new JsonArrayRequest(
                Request.Method.GET,
                baseUrl+path,
                data,
                onSuccess,
                onFailure){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // In case no data is passed in as a header explicitly
                final HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type","application/json");
                if(headers != null){
                    header.putAll(headers);
                }
                return header;
            }
        };
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                MY_MAX_RETRIES,
                MY_BACKOFF_MULTIPLIER));
        queue.add(objectRequest);
    }

    public void getRequest(
            String path,
            HashMap<String,String> headers,
            JSONObject data,
            Response.Listener<JSONObject> onSuccess,
            Response.ErrorListener onFailure
    ){
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                baseUrl+path,
                data,
                onSuccess,
                onFailure){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // In case no data is passed in as a header explicitly
                final HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type","application/json");
                if(headers != null){
                    header.putAll(headers);
                }
                return header;
            }
        };
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                MY_MAX_RETRIES,
                MY_BACKOFF_MULTIPLIER));
        queue.add(objectRequest);
    }
    public void patchRequest(
            String path,
            HashMap<String,String> headers,
            JSONObject data,
            Response.Listener<JSONObject> onSuccess,
            Response.ErrorListener onFailure
    ){
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                baseUrl+path,
                data,
                onSuccess,
                onFailure){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // In case no data is passed in as a header explicitly
                final HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type","application/json");
                if(headers != null){
                    header.putAll(headers);
                }
                return header;
            }
        };
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                MY_MAX_RETRIES,
                MY_BACKOFF_MULTIPLIER));
        queue.add(objectRequest);
    }

    public void deleteRequest(
            String path,
            HashMap<String,String> headers,
            JSONObject data,
            Response.Listener<JSONObject> onSuccess,
            Response.ErrorListener onFailure
    ){
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                baseUrl+path,
                data,
                onSuccess,
                onFailure){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // In case no data is passed in as a header explicitly
                final HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type","application/json");
                if(headers != null){
                    header.putAll(headers);
                }
                return header;
            }
        };
        objectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                MY_MAX_RETRIES,
                MY_BACKOFF_MULTIPLIER));
        queue.add(objectRequest);
    }

}
