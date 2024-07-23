package com.theteam.taskz.presentation.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserData;

public class SelectPlatformSync extends AppCompatActivity {
    private boolean notionSynced,githubSynced,jiraSynced,googleCalendarSynced;
    private LinearLayout google_calendar_button,github_button,notion_button,jira_button;

    @Override
    protected void onResume() {
        super.onResume();
        checkSyncStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.select_platform_sync_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        google_calendar_button = (LinearLayout) findViewById(R.id.google_calendar_button);
        github_button = (LinearLayout) findViewById(R.id.github_button);
        notion_button = (LinearLayout) findViewById(R.id.notion_button);
        jira_button = (LinearLayout) findViewById(R.id.jira_button);


        github_button.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), githubSynced? GithubProfileScreen.class:SyncGithub.class));
        });

        jira_button.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SyncJira.class));
        });

        notion_button.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SyncNotion.class));
        });

        checkSyncStatus();


    }

    private void checkSyncStatus(){
        notionSynced = UserData.notionAccount(getApplicationContext()) != null;
        githubSynced = UserData.githubAccount(getApplicationContext()) != null;
        jiraSynced = false;
        googleCalendarSynced = false;

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        github_button.removeAllViews();
        notion_button.removeAllViews();

        final View githubView = getLayoutInflater().inflate((githubSynced?R.layout.github_synced_platform:R.layout.github_signup_platform), null, false);
        final View notionView = getLayoutInflater().inflate((notionSynced?R.layout.notion_synced_platform:R.layout.notion_signup_platform), null, false);
        githubView.setLayoutParams(layoutParams);
        notionView.setLayoutParams(layoutParams);
        github_button.addView(githubView);
        notion_button.addView(notionView);
    }
}