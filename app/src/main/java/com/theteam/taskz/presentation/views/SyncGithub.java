package com.theteam.taskz.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Response;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.GithubAccount;
import com.theteam.taskz.data.models.TaskManager;
import com.theteam.taskz.data.models.UserData;
import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.domain.repositories.Github;
import com.theteam.taskz.domain.repositories.TaskRepository;

import java.util.ArrayList;

public class SyncGithub extends AppCompatActivity {

    public static int GITHUB_LOGIN = 2024;
    private TextInputFormField token_form;

    private TextView textView;

    private LinearLayout github_sign_in;
    private LoadableButton sync;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sync_github_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        token_form = (TextInputFormField) findViewById(R.id.token_form);
        textView = findViewById(R.id.issues);
        sync = (LoadableButton) findViewById(R.id.loadable_button);
        github_sign_in = (LinearLayout) findViewById(R.id.github_sign_in_button);

        textView.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), GithubSyncDircetions.class));
        });


        sync.setOnClickListener(view -> {
           sync();
        });

        github_sign_in.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), GithubWebView.class);
            startActivityForResult(i,GITHUB_LOGIN);
        });

    }

    final Response.Listener<ArrayList<Task>> gotTasks(){
        return tasks -> {
            TaskRepository taskRepository = new TaskRepository(getApplicationContext());

            for(final Task task: tasks){
                taskRepository.createTask(
                        task,
                        null,
                        jsonObject -> {
                            Log.v("API_RESPONSE", jsonObject.toString());
                            new TaskManager(getApplicationContext()).addTask(Task.fromJson(jsonObject.toString()), true);
                            sync.stopLoading();
                            Toast.makeText(getApplicationContext(), "Welcome " + UserData.githubAccount(getApplicationContext()).name, Toast.LENGTH_SHORT).show();
                            if (tasks.indexOf(task)==tasks.size()-1){
                                finish();
                            }
                        },
                        volleyError -> {
                            Log.v("API_RESPONSE", volleyError.toString());

                        }

                );
            }
        };
    }
    void sync(){
        sync.startLoading();
        new Handler().postDelayed(() -> {
            runOnUiThread(() -> {
                Github.validateUserToken(
                        token_form.getText().trim(),
                        this,
                        sync,
                        jsonObject -> {
                            Github.listAllIssues(UserData.githubAccount(getApplicationContext()), getApplicationContext(), gotTasks());
                        }
                        );
            });
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the github login was successful,
        // We set the text of the form_field to the token, then we run things again
        if(requestCode == GITHUB_LOGIN && resultCode == RESULT_OK && data != null){
            token_form.setText(data.getStringExtra("token").toString());
            sync();
        }
    }
}