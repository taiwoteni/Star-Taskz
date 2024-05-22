package com.theteam.taskz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.theteam.taskz.data.AuthenticationDataHolder;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;
import com.theteam.taskz.view_models.UnderlineTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private LoadableButton button;
    private TextInputFormField emailFormField, passwordFormField;
    private UnderlineTextView createAccount;

    private LinearLayout googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    final int RC_SIGN_IN = 200;

    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // View initializations
        emailFormField = findViewById(R.id.email_form);
        passwordFormField = findViewById(R.id.password_form);
        button = findViewById(R.id.loadable_button);
        createAccount = findViewById(R.id.create_account);
        googleSignInButton = findViewById(R.id.google_sign_in_button);

        createAccount.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
        });

        googleSignInButton.setOnClickListener(view -> {
            startGoogleSignIn();
        });

        // The On click Listener for the Loadable button
        button.setOnClickListener(view -> {
            // We load first then wait 1 seconds before the other codes are executed
            validate();

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    boolean validateEmail(){
        final String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(regex, emailFormField.getText());
    }
    boolean validatePassword(){

        return passwordFormField.getText().length()>= 8;
    }
    void showErrorMessage(final String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    void startGoogleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            // You can get user details from 'account' object.
            AuthenticationDataHolder.email = account.getEmail();
            AuthenticationDataHolder.password = "sTaR_TaSkZ@30_May@" + account.getEmail();
            googleSignOut();
            validateGoogleSignIn();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showErrorMessage("Google Sign In Failed");
        }
    }
    void googleSignOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    void validate(){
        button.startLoading();
        passwordFormField.setEnabled(false);
        emailFormField.setEnabled(false);

        Handler handler = new Handler();
        Handler runHandler = new Handler();
        handler.postDelayed(() -> {
            button.stopLoading();
            passwordFormField.setEnabled(true);
            emailFormField.setEnabled(true);
            if(!validateEmail()){
                showErrorMessage("Wrongly Formatted Email!");
                return;
            }
            if(!validatePassword()){
                showErrorMessage("Password should be up to 8 digits");
                return;
            }
            AuthenticationDataHolder.email = emailFormField.getText().trim();
            AuthenticationDataHolder.password = passwordFormField.getText().trim();
            load();
            runHandler.postDelayed(this::login, 3000);
        }, 1000);
    }
    void validateGoogleSignIn(){
        load();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
           runOnUiThread(() -> {
               login();
           });
        },2000);


    }

    void load(){
        dialog = new Dialog(this);
        View contentView = getLayoutInflater().inflate(R.layout.star_loading_dialog, null);
        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(false);
        dialog.show();
    }

    void showError(String message){
        if(dialog.isShowing()){
            dialog.dismiss();
        }

        View contentView = getLayoutInflater().inflate(R.layout.star_error_dialog, null);
        final LoadableButton loadableButton = contentView.findViewById(R.id.go_button);
        final TextView contentText = contentView.findViewById(R.id.content_text);

        contentText.setText(message);

        loadableButton.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(true);
        dialog.show();

    }
    void login(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject params = new JSONObject();
        try {
            params.put("email", AuthenticationDataHolder.email);
            params.put("password", AuthenticationDataHolder.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String endpoint = TaskManager.END_POINT + "auth/login";
        int method = Request.Method.POST;
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
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

                dialog.dismiss();
                if(statusCode!=200){
                    if(body.get("message").toString().toLowerCase().trim().contains("bad credential")){
                        showError("Sorry Pal.\nYou used a wrong email or password");
                    }
                }

            }

        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                dialog.dismiss();
                showError("Sorry pal, An unknown error occurred.");
                Log.v("API_RESPONSE", volleyError.toString());
            }
        };

        final JsonObjectRequest request = new JsonObjectRequest(method, endpoint, params, responseListener,errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(request);
    }
}