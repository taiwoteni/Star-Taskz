package com.theteam.taskz;

import com.theteam.taskz.AlarmManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;

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
        Uri soundUri = Uri.parse("android.resource://" + context.getApplicationContext().getPackageName() + "/" + R.raw.notification);


        NotificationChannel taskChannel = new NotificationChannel(model.id, "Star Taskz", NotificationManager.IMPORTANCE_HIGH);
        taskChannel.setDescription("Task Reminder Channel");
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build();
        taskChannel.setSound(soundUri, audioAttributes);
        nm.createNotificationChannel(taskChannel);


        Notification notif = new NotificationCompat.Builder(context.getApplicationContext(), model.id)
                .setContentTitle(model.name)
                .setContentText("Should start now, It's " + timeString)
                .setSmallIcon(R.drawable.task)
                .setLargeIcon(largeIcon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(false)
                .setSound(soundUri)
                .setChannelId(model.id)
                .setColor(context.getApplicationContext().getResources().getColor(R.color.themeColor))
                .setAutoCancel(true)
                .build();
        nm.notify(model.notifIdExists? model.notifId:AlarmManager.NOTIF_ID, notif);
        if(!model.notifIdExists){
           AlarmManager.NOTIF_ID++;
        }

        AlarmManager.speech.speak("This is a Reminder From Star Tasks", TextToSpeech.QUEUE_ADD, null, null);
        AlarmManager.speech.speak("Your Task '" + model.name + "' should start now.", TextToSpeech.QUEUE_ADD, null, null);
        AlarmManager.speech.speak("It is " + timeString, TextToSpeech.QUEUE_ADD, null, null);
        AlarmManager.speech.speak("Please, Do not forget your Task", TextToSpeech.QUEUE_ADD, null, null);

        Intent i = new Intent(context.getApplicationContext(), TaskReminder.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(intent.getExtras());
        context.getApplicationContext().startActivity(i);

    }




}