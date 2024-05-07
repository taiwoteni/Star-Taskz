package com.example.taskmanagerfeatures;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginPage extends AppCompatActivity {
    EditText login_email, login_password;
    TextView login_button;
    CardView login_with_google, login_with_facebook, login_with_apple;
    String email_address, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        login_with_google = findViewById(R.id.login_with_google);
        login_with_facebook = findViewById(R.id.login_with_facebook);
        login_with_apple = findViewById(R.id.login_with_apple);

        email_address = login_email.getText().toString();
        password = login_password.getText().toString();
    }
}