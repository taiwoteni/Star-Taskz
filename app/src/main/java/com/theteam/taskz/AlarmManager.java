package com.theteam.taskz;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;

public class AlarmManager {
    public static int NOTIF_ID = 0;
    static final int ALARM_REQ_CODE = 100;
    private static PendingIntent pi;

    private android.app.AlarmManager star_taskz;

    private long time;
    private Context application_context,activity_context;

    public AlarmManager(Context application_context,  Context activity_context){
        this.application_context = application_context;
        this.activity_context = activity_context;
        this.time = time;
    }

    public void setAlarm(TaskModel model){
        Bundle b = new Bundle();
        b.putString("TASK", new Gson().toJson(model.toJson()));
        Intent intent = new Intent(activity_context, AlarmsReceiver.class);
        intent.putExtras(b);
        final Calendar calendar = (Calendar) model.date.clone();

        //Because there's always a 2 minute lag.
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)-1);
        time = calendar.getTimeInMillis();
        pi = PendingIntent.getBroadcast(application_context, 0, intent, PendingIntent.FLAG_MUTABLE);

        star_taskz = (android.app.AlarmManager) activity_context.getSystemService(Context.ALARM_SERVICE);
        star_taskz.set(android.app.AlarmManager.RTC_WAKEUP, time, pi);

        final SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        final String timeString = format.format(model.date.getTime()).toUpperCase();

        Bitmap largeIcon = BitmapFactory.decodeResource(activity_context.getResources(), R.drawable.taskz_round);

        NotificationManager nm = (NotificationManager) application_context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel taskChannel = new NotificationChannel(model.id, "Star Taskz", NotificationManager.IMPORTANCE_HIGH);
        taskChannel.setDescription("Task Reminder Channel");
        nm.createNotificationChannel(taskChannel);


        Notification notif = new NotificationCompat.Builder(activity_context, model.id)
                .setContentTitle(model.name)
                .setContentText("Task would start by " + timeString)
                .setSmallIcon(R.drawable.task)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setOngoing(false)
                .setChannelId(model.id)
                .setColor(activity_context.getResources().getColor(R.color.themeColor))
                .setAutoCancel(true)
                .build();
        nm.notify(NOTIF_ID, notif);
        NOTIF_ID++;



    }

}
