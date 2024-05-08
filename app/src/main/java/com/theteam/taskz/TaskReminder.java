package com.theteam.taskz;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.view_models.LoadableButton;

import java.util.HashMap;

public class TaskReminder extends AppCompatActivity {

    private LoadableButton complete_button,ignore_button;
    private TextView task_name;
    private TaskModel task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_reminder);
        complete_button = findViewById(R.id.complete_button);
        ignore_button = findViewById(R.id.ignore_button);
        task_name = findViewById(R.id.task_name);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_ALARM_ALERT_URI);
        mp.setVolume(0.6f, 0.6f);
        mp.start();

        Bundle b = getIntent().getExtras();
        HashMap<String,Object> taskJson = new Gson().fromJson(b.getString("TASK"), new TypeToken<HashMap<String,Object>>(){}.getType());

        task = new TaskModel(taskJson);

        task_name.setText(task.name);

        complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.updateStatus(TaskStatus.Completed);
                new TaskManager(getApplicationContext()).updateTask(task);
                mp.stop();
                finish();
            }
        });
        ignore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.updateStatus(TaskStatus.Pending);
                new TaskManager(getApplicationContext()).updateTask(task);
                mp.stop();
                finish();
            }
        });

    }
}