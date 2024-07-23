package com.theteam.taskz.data.datasources;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class JobScheduleHelper {

    public static void scheduleJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(1,
                new ComponentName(context, WorkspaceService.class))
                .setPeriodic(3000) // Repeat every 3 seconds
                .build();

        jobScheduler.schedule(jobInfo);
    }
}

