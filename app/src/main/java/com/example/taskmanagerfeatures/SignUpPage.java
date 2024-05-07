package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class SignUpPage extends AppCompatActivity {
    EditText signup_first_name, signup_last_name, signup_date_of_birth, signup_email, signup_password;
    Button signup_button;
    RelativeLayout signup_with_google;
    String first_name, last_name, date_of_birth, email_address, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        signup_first_name = findViewById(R.id.signup_first_name);
//        signup_last_name = findViewById(R.id.signup_last_name);
        signup_date_of_birth = findViewById(R.id.signup_date_of_birth);
        signup_email = findViewById(R.id.signup_email);
        signup_password = findViewById(R.id.signup_password);
        //signup_button = findViewById(R.id.signup_button);
        signup_with_google = findViewById(R.id.signup_with_google);

        first_name = signup_first_name.getText().toString();
        last_name = signup_last_name.getText().toString();
        date_of_birth = signup_date_of_birth.getText().toString();
        email_address = signup_email.getText().toString();
        password = signup_password.getText().toString();
    }
}