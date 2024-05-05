package com.theteam.taskz;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;

import java.util.Calendar;
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
                String monthString = String.valueOf(_month);


                final String formatted = String.valueOf(dayString.length()!=2? "0" + dayString: day) + "/" + String.valueOf(monthString.length()!=2? "0" + monthString: _month) + "/" + String.valueOf(_year);

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
            showErrorMessage("Welcome " + account.getDisplayName());
            signout();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            showErrorMessage("Google Sign In Failed");
        }
    }

    void signout(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Executor) this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
}
