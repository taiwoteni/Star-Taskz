package com.theteam.taskz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;
import com.theteam.taskz.view_models.UnderlineTextView;

import java.util.Timer;
import java.util.TimerTask;

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
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        button.stopLoading();
                    });
                }
            }, 2500);

        });
    }
}