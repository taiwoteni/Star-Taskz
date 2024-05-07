package com.theteam.taskz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;
import com.theteam.taskz.view_models.UnderlineTextView;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private LoadableButton button;
    private TextInputFormField emailFormField, passwordFormField;
    private UnderlineTextView createAccount;

    private LinearLayout googleSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    final int RC_SIGN_IN = 200;

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
            // We load first then wait 2.5 seconds before the other codes are executed
            button.startLoading();
            passwordFormField.setEnabled(false);
            emailFormField.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                button.stopLoading();
                if(!validateEmail()){
                    showErrorMessage("Wrongly Formatted Email!");
                    passwordFormField.setEnabled(true);
                    emailFormField.setEnabled(true);
                    return;
                }
                if(!validatePassword()){
                    showErrorMessage("Password should be up to 8 digits");
                    passwordFormField.setEnabled(true);
                    emailFormField.setEnabled(true);
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }, 2500);

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
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
}