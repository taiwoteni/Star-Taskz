package com.theteam.taskz;

import com.theteam.taskz.AlarmManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Locale;

public class AlarmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        HashMap<String,Object> taskJson = new Gson().fromJson(bundle.getString("TASK"), new TypeToken<HashMap<String,Object>>(){}.getType());

        final TaskModel model = new TaskModel(taskJson);
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        final String timeString = format.format(model.date.getTime()).toUpperCase();

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.taskz_round);

        NotificationManager nm = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel taskChannel = new NotificationChannel(model.id, "Star Taskz", NotificationManager.IMPORTANCE_HIGH);
        taskChannel.setDescription("Task Reminder Channel");
        nm.createNotificationChannel(taskChannel);


        Notification notif = new NotificationCompat.Builder(context.getApplicationContext(), model.id)
                .setContentTitle(model.name)
                .setContentText("Should start now, It's " + timeString)
                .setSmallIcon(R.drawable.task)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(false)
                .setChannelId(model.id)
                .setColor(context.getApplicationContext().getResources().getColor(R.color.themeColor))
                .setAutoCancel(true)
                .build();
        nm.notify(model.notifIdExists? model.notifId:AlarmManager.NOTIF_ID, notif);
        if(!model.notifIdExists){
           AlarmManager.NOTIF_ID++;
        }

        Intent i = new Intent(context.getApplicationContext(), TaskReminder.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(intent.getExtras());
        context.getApplicationContext().startActivity(i);
    }


}