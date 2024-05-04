package com.theteam.taskz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;
import com.theteam.taskz.view_models.UnderlineTextView;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private LoadableButton button;
    private TextInputFormField emailFormField, passwordFormField;
    private UnderlineTextView createAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // View initializations
        emailFormField = findViewById(R.id.email_form);
        passwordFormField = findViewById(R.id.password_form);
        button = findViewById(R.id.loadable_button);
        createAccount = findViewById(R.id.create_account);

        createAccount.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
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
            }, 2500);

        });
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
}