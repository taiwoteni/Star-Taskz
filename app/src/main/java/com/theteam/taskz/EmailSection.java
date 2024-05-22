package com.theteam.taskz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.theteam.taskz.data.ViewPagerDataHolder;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.models.UserModel;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;
import com.theteam.taskz.view_models.UnderlineTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EmailSection extends Fragment {

    private LoadableButton button;
    private LinearLayout googleSignInButton;
    private TextInputFormField emailForm,passwordForm,confirmPasswordForm;
    private UnderlineTextView textView;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    final int RC_SIGN_IN = 200;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.email_section, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        button = view.findViewById(R.id.loadable_button);
        textView = view.findViewById(R.id.back);
        emailForm = view.findViewById(R.id.email_form);
        passwordForm = view.findViewById(R.id.password_form);
        confirmPasswordForm = view.findViewById(R.id.confirm_password_form);
        googleSignInButton = view.findViewById(R.id.google_sign_in_button);

        googleSignInButton.setOnClickListener(view1 -> {
            startGoogleSignIn();
        });

        if(AuthenticationDataHolder.email != null){
            emailForm.setText(AuthenticationDataHolder.email);
        }

        textView.setOnClickListener(view1 -> {
            ViewPagerDataHolder.viewPager.setCurrentItem(0, true);
        });

        button.setOnClickListener(view1 -> {
            button.startLoading();
            disableAllInput();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                button.stopLoading();
                if(!validateEmail()){
                    showErrorMessage("Email Wrongly Formatted !!");
                    enableAllInput();
                    return;
                }
                if(!validatePassword()){
                    showErrorMessage("Password should be at least 8 digits");
                    enableAllInput();
                    return;
                }
                if(!passwordForm.getText().trim().equals(confirmPasswordForm.getText().trim())){
                    showErrorMessage("Passwords are not matching");
                    enableAllInput();
                    return;
                }
                enableAllInput();
                AuthenticationDataHolder.email = emailForm.getText().trim();
                AuthenticationDataHolder.password = passwordForm.getText().trim();

                createAccount();
            }, 2500);
        });
    }


    void disableAllInput(){
        emailForm.setEnabled(false);
        passwordForm.setEnabled(false);
        confirmPasswordForm.setEnabled(false);
    }
    void enableAllInput(){
        emailForm.setEnabled(true);
        passwordForm.setEnabled(true);
        confirmPasswordForm.setEnabled(true);
    }

    boolean validateEmail(){
        final String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(regex, emailForm.getText());
    }
    boolean validatePassword(){

        return passwordForm.getText().length()>= 8;
    }
    void showErrorMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
            AuthenticationDataHolder.firstName = account.getGivenName();
            AuthenticationDataHolder.lastName = account.getFamilyName() == null? "":account.getFamilyName();
            AuthenticationDataHolder.email = account.getEmail();
            AuthenticationDataHolder.password = "sTaR_TaSkZ@30_May@" + account.getEmail();
            googleSignOut();
            createAccount();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showErrorMessage("Google Sign In Failed");
        }
    }
    void googleSignOut(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


    void createAccount(){
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
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



        String endpoint = TaskManager.END_POINT + "auth/register";
        int method = Request.Method.POST;
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.v("API_RESPONSE", jsonObject.toString());

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

                //We get convert the json body to a map.
                Gson gson = new Gson();
                Type mapType = new TypeToken<HashMap<String,Object>>(){}.getType();
                HashMap<String,Object> body = gson.fromJson(jsonObject.toString(),mapType);
                int statusCode = (int) Double.parseDouble(body.getOrDefault("statusCode", 400).toString());

                if(statusCode == 200){
                    String userDataString = body.get("ourUsers").toString();
                    HashMap<String,Object> userData = gson.fromJson(userDataString, mapType);
                    UserModel.saveUserData(userData, requireActivity());
                    AuthenticationDataHolder.clear();
                    showErrorMessage("Registered In Successfully");
                    startActivity(new Intent(requireActivity().getApplicationContext(),HomeActivity.class).putExtra("first",""));
                }
                if(statusCode == 500){
                    if(body.get("error").toString().contains("Duplicate entry")){
                        showErrorMessage("User already exists");
                    }
                }
            }

        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
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
