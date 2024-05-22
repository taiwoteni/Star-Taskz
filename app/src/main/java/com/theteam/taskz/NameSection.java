package com.theteam.taskz;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class NameSection extends Fragment {
    private LoadableButton button;
    private TextView alreadyHaveAccount;
    private TextInputFormField firstNameForm, lastNameForm, dateOfBirthForm;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    final int RC_SIGN_IN = 200;

    private LinearLayout googleSignInButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.name_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        button = view.findViewById(R.id.loadable_button);
        alreadyHaveAccount = view.findViewById(R.id.have_account);
        firstNameForm = view.findViewById(R.id.first_name_form);
        lastNameForm = view.findViewById(R.id.last_name_form);
        dateOfBirthForm = view.findViewById(R.id.dob_form);
        googleSignInButton = view.findViewById(R.id.google_sign_in_button);

        googleSignInButton.setOnClickListener(view1 -> {
            startGoogleSignIn();
        });

        if(AuthenticationDataHolder.firstName != null){
            firstNameForm.setText(AuthenticationDataHolder.firstName);
        }
        if(AuthenticationDataHolder.lastName != null){
            lastNameForm.setText(AuthenticationDataHolder.lastName);
        }
        if(AuthenticationDataHolder.dob != null){
            dateOfBirthForm.setText(AuthenticationDataHolder.dob);
        }


        dateOfBirthForm.setOnClickListener(view1 -> {
            showDatePicker();
        });

        alreadyHaveAccount.setOnClickListener(view1 -> {
            requireActivity().finish();
        });

        button.setOnClickListener(view1 -> {
            button.startLoading();
            disableAllInput();
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            button.stopLoading();
                            if(!validateName(firstNameForm.getText())){
                                showErrorMessage("First Name is Empty");
                                enableAllInput();
                                return;
                            }
                            if(!validateName(lastNameForm.getText())){
                                showErrorMessage("Last Name is Empty");
                                enableAllInput();
                                return;
                            }
                            if(!validateName(dateOfBirthForm.getText())){
                                showErrorMessage("Date Of Birth is Empty");
                                enableAllInput();
                                return;
                            }
                            enableAllInput();
                            AuthenticationDataHolder.firstName = firstNameForm.getText().trim();
                            AuthenticationDataHolder.lastName = lastNameForm.getText().trim();
                            AuthenticationDataHolder.dob = dateOfBirthForm.getText().trim();


                            ViewPagerDataHolder.viewPager.setCurrentItem(1, true);
                        }
                    },
                    2500
            );
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


    void disableAllInput(){
        firstNameForm.setEnabled(false);
        lastNameForm.setEnabled(false);
        dateOfBirthForm.setEnabled(false);
    }
    void enableAllInput(){
        firstNameForm.setEnabled(true);
        lastNameForm.setEnabled(true);
        dateOfBirthForm.setEnabled(true);
    }
    boolean validateName(final String name){
        return !name.trim().isEmpty();
    }

    void showDatePicker(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int day) {
                String dayString = String.valueOf(day);
                String monthString = String.valueOf(_month+1);


                final String formatted = String.valueOf(String.valueOf(_year) + "-"+ String.valueOf(monthString.length()!=2? "0" + monthString: _month) + "-" +  String.valueOf(dayString.length()!=2? "0"+dayString: dayString));

                AuthenticationDataHolder.dob = formatted;
                dateOfBirthForm.setText(formatted);
            }
        },year,month,dayOfMonth);
        datePickerDialog.show();
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
