package com.theteam.taskz.services;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.HomeActivity;
import com.theteam.taskz.R;
import com.theteam.taskz.data.AuthenticationDataHolder;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.models.UserModel;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.UnderlineTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** @noinspection ExtractMethodRecommender, DataFlowIssue */
public class ApiService {
    public static String openWeatherKey = "9e77dbfd2cc45955ad36269ba273fa69";
    public static String END_POINT = "https://startaskzbackend-production.up.railway.app/";

    private static Dialog dialog;

    private boolean registration = false;

    private static RequestQueue queue;
    private final Context context;

    private Activity activity;
    private LayoutInflater inflater;

    public ApiService(Activity activity, LayoutInflater inflater){
        this.inflater = inflater;
        this.context = activity;
        this.activity = activity;
    }
    public ApiService(Context context){
        this.context = context;
    }

    public static void addTask(Context context, TaskModel model) throws JSONException {
        if(queue == null){
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        final UserModel userModel = new UserModel(context);
        final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("API_RESPONSE", jsonObject.toString());
                final TaskManager manager = new TaskManager(context);
                final HashMap<String,Object> modelData = model.toJson();

                //We tie the ID given from the API to the Model.
                //This is because we would need the same ID when we try to update a task at the API EndPoint.
                try {
                    modelData.replace("globalId", String.valueOf(jsonObject.get("id")));
                    //Then we update that task and save it in the Device Storage.
                    Gson gson = new Gson();
                    SharedPreferences preferences = context.getSharedPreferences("GLOBAL", Context.MODE_PRIVATE);
                    final String jsonString = preferences.getString("tasks", "[]");
                    Type listType = new com.google.gson.reflect.TypeToken<ArrayList<Map<String, Object>>>(){}.getType();
                    ArrayList<Map<String, Object>> list = gson.fromJson(jsonString, listType);

                    final HashMap<String,Object> maps = modelData;

                    int index = 0;
                    for(final Map<String,Object> _model: list){
                        if(Objects.equals(_model.get("id"), model.id)){
                            index = list.indexOf(_model);
                        }
                    }

                    for(String key: maps.keySet()) {
                        if (list.get(index).containsKey(key)) {
                            list.get(index).replace(key, maps.get(key));
                        } else {
                            list.get(index).put(key, maps.get(key));
                        }
                    }

                    preferences.edit().putString("tasks", gson.toJson(list)).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                final String message = volleyError.getMessage();
                Log.e("API_RESPONSE", message==null? volleyError.toString(): message);
            }
        };

        final int method = Request.Method.POST;

        final String endpoint = END_POINT + "user/create-task/" + userModel.uid();

        Log.d("API_RESPONSE", model.toJsonObject().toString());


        final JsonObjectRequest request = new JsonObjectRequest(
                method,
                endpoint,
                model.toJsonObject(),
                responseListener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + new UserModel(context).authToken());
                return headers;
            }
        };

        queue.add(request);
    }
    public static void updateTask(Context context, TaskModel model) throws JSONException {
        if(queue == null){
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        final UserModel userModel = new UserModel(context);
        final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("API_RESPONSE", jsonObject.toString());
            }
        };
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("API_RESPONSE", volleyError.toString());
            }
        };

        final int method = Request.Method.PUT;

        final String endpoint = END_POINT + "user/update-task/" + userModel.uid() + "/" + model.globalId;


        final JsonObjectRequest request = new JsonObjectRequest(
                method,
                endpoint,
                model.toJsonObject(),
                responseListener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + userModel.authToken());
                return headers;
            }
        };

        queue.add(request);
    }
    public static void deleteTask(Context context, TaskModel model){
        if(queue == null){
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        final UserModel userModel = new UserModel(context);
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.v("API_RESPONSE", result);
            }
        };
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("API_RESPONSE", volleyError.toString());
            }
        };

        final int method = Request.Method.DELETE;

        final String endpoint = END_POINT + "user/delete-task/" + userModel.uid() + "/" + model.globalId;


        final StringRequest request = new StringRequest(
                method,
                endpoint,
                responseListener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userModel.authToken());
                return headers;
            }
        };

        queue.add(request);
    }
    public void saveTasks(boolean show) throws JSONException {
        if(show){
            showStarLoading("Loading your tasks from the server.\nPls hold on🙃.");
        }
        if(queue == null){
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        final UserModel userModel = new UserModel(context);
        final Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.v("API_RESPONSE", jsonArray.toString());
                final TaskManager manager = new TaskManager(context);
                manager.clearTasks();
                for(int i = 0; i<jsonArray.length(); i++){
                    try {
                        final JSONObject object = jsonArray.getJSONObject(i);
                        object.put("fakeId", "#TASK-" + manager.getTasks().size());
                        final TaskModel model = TaskModel.fromJsonObject(object);
                        manager.addTask(model,false);
                    } catch (JSONException e) {
                        Log.e("API_RESPONSE", e.toString());
                        e.printStackTrace();
                    }
                }
                if(show){
                    showStarSuccess(jsonArray.length()==0? "You currently have no tasks on the cloud.\nDon't hesitate to create them😊":"I Successfully loaded your tasks from the cloud😊.", "THANKS");

                }
            }
        };
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(show){
                    showStarError("Unable to load your tasks.\nCheck your internet");
                }
                Log.e("API_RESPONSE", volleyError.toString());
            }
        };

        final int method = Request.Method.GET;

        final String endpoint = END_POINT + "user/get-tasks/" + userModel.uid();


        final JsonArrayRequest request = new JsonArrayRequest(
                method,
                endpoint,
                null,
                responseListener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + userModel.authToken());
                return headers;
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            queue.add(request);
        }, 4000);

    }
    public void loginAccount(){
        if(queue == null){
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }

        JSONObject params = new JSONObject();
        try {
            params.put("email", AuthenticationDataHolder.email);
            params.put("password", AuthenticationDataHolder.password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String endpoint = END_POINT + "auth/login";
        int method = Request.Method.POST;

        final JsonObjectRequest request = new JsonObjectRequest(method, endpoint, params, loginListener(),errorListener()){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(request);

    }

    public void createAccount(){
        registration = true;
        showStarLoading("Creating your account.\nPls hold on🙃.");
        if(queue == null){
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }

        /**
         * Normally, By how things are,
         * After Registration, The user is meant to login--inorder to generate token for API Services.
         * But, to aid good user experience, all that would be done spontaneously.
         * **/

        // First we Create Account
        JSONObject params = new JSONObject();
        try {
            params.put("firstName", AuthenticationDataHolder.firstName);
            params.put("lastName", AuthenticationDataHolder.lastName);
            params.put("dateOfBirth", AuthenticationDataHolder.dob);
            params.put("email", AuthenticationDataHolder.email);
            params.put("password", AuthenticationDataHolder.password);
            params.put("role", "USER");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String endpoint = END_POINT + "auth/register";
        int method = Request.Method.POST;
        final HashMap<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        final JsonObjectRequest request = new JsonObjectRequest(method, endpoint, params, registrationListener(),errorListener()){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        queue.add(request);
    }

    public void checkAndSynced(){
        if(queue == null){
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        final UserModel userModel = new UserModel(context);

        final Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.v("API_RESPONSE", jsonArray.toString());

                final ArrayList<TaskModel> models =new TaskManager(context).getTasks();

                try {
                    for(int i = 0; i<models.size(); i++){
                        final TaskModel model = models.get(i);
                        //Not Added
                        if(model.globalId.equals(model.id)){
                            addTask(context, model);
                        }
                        else{
                            updateTask(context, models.get(i));
                        }
                    }
                    saveTasks(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("API_RESPONSE", volleyError.toString());
            }
        };

        final int method = Request.Method.GET;

        final String endpoint = END_POINT + "user/get-tasks/" + userModel.uid();


        final JsonArrayRequest request = new JsonArrayRequest(
                method,
                endpoint,
                null,
                responseListener,
                errorListener
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + userModel.authToken());
                return headers;
            }
        };

        queue.add(request);

    }
    public void refreshToken(){
        if(queue == null){
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.v("API_RESPONSE", volleyError.toString());
                volleyError.printStackTrace();
            }
        };

        JSONObject params = new JSONObject();
        try {
            params.put("email", AuthenticationDataHolder.email);
            params.put("password", AuthenticationDataHolder.password);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        String endpoint = END_POINT + "auth/login";
        int method = Request.Method.POST;

        final JsonObjectRequest request = new JsonObjectRequest(method, endpoint, params, refreshListener(),errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(request);

    }
    private Response.Listener<JSONObject> loginListener(){
        /**
         * We are using this response listener as a preset listener for login.
         * It also handles all the processes involving dialogs, That's why context was asked for.
         * **/

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("API_RESPONSE", jsonObject.toString());
                //We get convert the json body to a map.

                //We try to remove the password from the request body if the user successfully registered cos of:
                //1. Security.
                //2. A wierd escape character in the encrypted password like : '\/' causing string format problems.

                if(jsonObject.has("ourUsers")){
                    try {
                        jsonObject.getJSONObject("ourUsers").remove("password");
                    } catch (JSONException e) {
                        Log.v("API_RESPONSE", "Unable to remove password parameter");
                    }
                }

                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<String,Object>>(){}.getType();
                HashMap<String,Object> body = gson.fromJson(jsonObject.toString(),mapType);
                int statusCode = (int) Double.parseDouble(body.getOrDefault("statusCode", 400).toString());

                if(statusCode!=200){
                    if(body.get("message").toString().toLowerCase().trim().contains("bad credential")){
                        showStarError("Sorry Pal.\nNot sure but I think you used a wrong email or password.");
                    }
                    else if(body.get("message").toString().toLowerCase().trim().contains("no value present")){
                        showStarError("Sorry pal. I just ran into an error.\nI don't think this user exists.");
                    }
                    else{
                        showStarError("Sorry pal.\nAn unknown error occurred.\nTry again");
                    }
                }
                if(statusCode==200){

                    // Because the expiration time of the token is 24Hrs
                    //We set a parameter called tokenExpiration to a formatted String of 1 day from now.
                    Calendar date = Calendar.getInstance();
                    final int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
                    final int year = date.get(Calendar.YEAR);
                    boolean isLeapYear = date.get(Calendar.YEAR)%4 ==0;
                    boolean isLastDay = isLeapYear? dayOfYear==366:dayOfYear==365;
                    date.set(Calendar.DAY_OF_YEAR, isLastDay? 1: date.get(Calendar.DAY_OF_YEAR)+1);
                    date.set(Calendar.YEAR, isLastDay? year+1:year);
                    date.set(Calendar.SECOND,0);
                    date.set(Calendar.MINUTE,0);

                    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

                    body.put("tokenExpiration", format.format(date.getTime()).toString());
                    body.put("id", body.get("userId"));
                    body.put("authToken", body.get("token"));
                    body.put("password", AuthenticationDataHolder.password);

                    // Then we remove the original data was derived from or not needed in the response body.
                    body.remove("statusCode");
                    body.remove("message");
                    body.remove("token");
                    body.remove("role");
                    body.remove("expirationTime");

                    Log.i("API", body.get("tokenExpiration").toString());

                    if(dialog!=null){
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }

                    // Then we save the data
                    UserModel.saveUserData(body, context.getApplicationContext());
                    Intent intent = new Intent(context.getApplicationContext(), HomeActivity.class);
                    if(!registration){
                        intent.putExtra("logged in", "");
                    }
                    else{
                        intent.putExtra("first", true);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }

            }

        };
    }
    private Response.Listener<JSONObject> refreshListener(){
        /**
         * We are using this response listener as a preset listener for login.
         * It also handles all the processes involving dialogs, That's why context was asked for.
         * **/

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("API_RESPONSE", jsonObject.toString());
                //We get convert the json body to a map.

                //We try to remove the password from the request body if the user successfully registered cos of:
                //1. Security.
                //2. A wierd escape character in the encrypted password like : '\/' causing string format problems.

                if(jsonObject.has("ourUsers")){
                    try {
                        jsonObject.getJSONObject("ourUsers").remove("password");
                    } catch (JSONException e) {
                        Log.v("API_RESPONSE", "Unable to remove password parameter");
                    }
                }

                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<String,Object>>(){}.getType();
                HashMap<String,Object> body = gson.fromJson(jsonObject.toString(),mapType);
                int statusCode = (int) Double.parseDouble(body.getOrDefault("statusCode", 400).toString());


                if(statusCode==200){

                    // Because the expiration time of the token is 24Hrs
                    //We set a parameter called tokenExpiration to a formatted String of 1 day from now.
                    Calendar date = Calendar.getInstance();
                    final int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
                    final int year = date.get(Calendar.YEAR);
                    boolean isLeapYear = date.get(Calendar.YEAR)%4 ==0;
                    boolean isLastDay = isLeapYear? dayOfYear==365:dayOfYear==366;
                    date.set(Calendar.DAY_OF_YEAR, isLastDay? 1: date.get(Calendar.DAY_OF_YEAR)+1);
                    date.set(Calendar.YEAR, isLastDay? year+1:year);

                    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    body.put("tokenExpiration", format.format(date.getTime()));
                    body.put("id", body.get("userId"));
                    body.put("authToken", body.get("token"));
                    body.put("password", AuthenticationDataHolder.password);

                    // Then we remove the original data was derived from or not needed in the response body.
                    body.remove("statusCode");
                    body.remove("message");
                    body.remove("token");
                    body.remove("role");
                    body.remove("expirationTime");


                    // Then we save the data
                    UserModel.saveUserData(body, context.getApplicationContext());

                }

            }

        };
    }

    private Response.Listener<JSONObject> registrationListener(){
        /**
         * We are using this response listener as a preset listener for registrations.
         * It also handles all the processes involving dialogs, That's why context was asked for.
         * Once registration is done, we login to get auth token
         * **/
        registration = true;

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("API_RESPONSE", jsonObject.toString());

                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<String,Object>>(){}.getType();
                HashMap<String,Object> body = gson.fromJson(jsonObject.toString(),mapType);
                int statusCode = (int) Double.parseDouble(body.getOrDefault("statusCode", 400).toString());


                if(statusCode==200){
                    loginAccount();
                }
                else{
                    if(body.get("error").toString().contains("Duplicate entry")){
                        showStarError("User already exists");
                    }
                    else{
                        showStarError("Sorry pal, An unknown error occurred.");
                    }

                }

            }

        };
    }

    private Response.ErrorListener errorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showStarError("Sorry pal, An unknown error occurred.");
                Log.v("API_RESPONSE", volleyError.toString());
                volleyError.printStackTrace();
            }
        };
    }

    private void showStarError(String message){
        if(dialog!= null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        else{
            dialog = new Dialog(context);
        }


        View contentView = inflater.inflate(R.layout.star_error_dialog, null);
        final LoadableButton loadableButton = contentView.findViewById(R.id.go_button);
        final TextView contentText = contentView.findViewById(R.id.content_text);

        contentText.setText(message);

        loadableButton.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(true);
        try{
            dialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private void showStarSuccess(String message, String label) {
        if(dialog!= null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        else{
            dialog = new Dialog(context);
        }


        View contentView = inflater.inflate(R.layout.star_intro_dialog, null);
        final LoadableButton loadableButton = contentView.findViewById(R.id.go_button);
        final TextView contentText = contentView.findViewById(R.id.content_text);
        final TextView contentTitle = contentView.findViewById(R.id.content_title);
        final UnderlineTextView skipText = contentView.findViewById(R.id.skip_button);


        contentTitle.setText("Hey There!");
        contentText.setText(message);
        loadableButton.setText(label);
        skipText.setVisibility(View.GONE);

        loadableButton.setOnClickListener(view -> {
            Intent intent = activity.getIntent();
            if(intent.hasExtra("logged in")){
                intent.removeExtra("logged in");
            }
            dialog.dismiss();
        });

        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(true);
        try{
            dialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private void showStarLoading(String message){
        if(dialog!= null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        else{
            dialog = new Dialog(context);
        }


        View contentView = inflater.inflate(R.layout.star_loading_dialog, null);
        final TextView contentText = contentView.findViewById(R.id.content_text);
        contentText.setText(message);

        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(false);
        try{
            dialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}
