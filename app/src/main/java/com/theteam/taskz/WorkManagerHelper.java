package com.theteam.taskz;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.theteam.taskz.data.datasources.WorkspaceService;

import java.util.concurrent.TimeUnit;

public class WorkManagerHelper {

    public static void scheduleWork() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                WorkspaceService.class, 3, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance().enqueue(periodicWorkRequest);
    }
}

