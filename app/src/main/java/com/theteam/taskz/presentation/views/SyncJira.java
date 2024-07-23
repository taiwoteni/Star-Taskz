package com.theteam.taskz.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theteam.taskz.R;
import com.theteam.taskz.domain.repositories.Github;

public class SyncJira extends AppCompatActivity {
    public static int JIRA_LOGIN = 2224;
    private TextInputFormField token_form;
    private LinearLayout jira_sign_in;
    private LoadableButton sync;

    private SplashRefreshLayout splash_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sync_jira);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        token_form = (TextInputFormField) findViewById(R.id.token_form);
        sync = (LoadableButton) findViewById(R.id.loadable_button);
        jira_sign_in = (LinearLayout) findViewById(R.id.jira_sign_in_button);
        splash_layout = (SplashRefreshLayout) findViewById(R.id.splash_layout);

        sync.setOnClickListener(view -> {
            sync();
        });

        jira_sign_in.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), JiraWebView.class);
            startActivityForResult(i,JIRA_LOGIN);
        });



    }

    void sync(){
        sync.startLoading();
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
//                Github.validateUserToken(token_form.getText().trim(), this,sync);
            });
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the github login was successful,
        // We set the text of the form_field to the token, then we run things again
        if(requestCode == JIRA_LOGIN && resultCode == RESULT_OK && data != null){
            token_form.setText(data.getStringExtra("token").toString());
            sync();
        }
    }

}