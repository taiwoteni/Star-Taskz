package com.theteam.taskz.receivers;

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
import android.speech.tts.Voice;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.HomeActivity;
import com.theteam.taskz.R;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.utilities.AlarmManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class AlarmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        HashMap<String,Object> taskJson = new Gson().fromJson(bundle.getString("TASK"), new TypeToken<HashMap<String,Object>>(){}.getType());

        final TaskModel model = new TaskModel(taskJson);
        final SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
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

        Intent i = new Intent(context.getApplicationContext(), HomeActivity.class);
        PendingIntent bubbleIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,i,PendingIntent.FLAG_MUTABLE, bundle);



        final String desc = "com.theteam.taskz.STAR_REMINDER";
        Person chatPerson = new Person.Builder()
                .setName("Star✨")
                .setIcon(IconCompat.createWithResource(context.getApplicationContext(), R.drawable.taskz_round))
                .setImportant(true)
                .build();

        ShortcutInfoCompat shortcut =  new ShortcutInfoCompat.Builder(context.getApplicationContext(), "STAR_REMINDER")
                .setCategories(Collections.singleton(desc))
                .setIcon(IconCompat.createWithResource(context.getApplicationContext(), R.drawable.taskz_round))
                .setIntent(new Intent(Intent.ACTION_DEFAULT))
                .setLongLived(true)
                .setShortLabel(Objects.requireNonNull(chatPerson.getName()))
                .build();
        ShortcutManagerCompat.pushDynamicShortcut(context.getApplicationContext(), shortcut);


        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(chatPerson)
                .addMessage("Your task '" + model.name + "' should start now 😊", model.date.getTimeInMillis(), chatPerson);

        NotificationCompat.BubbleMetadata bubbleData = new NotificationCompat.BubbleMetadata.Builder(bubbleIntent,
                IconCompat.createWithResource(context.getApplicationContext(), R.drawable.taskz_round))
                .setIcon(IconCompat.createWithResource(context.getApplicationContext(), R.drawable.taskz_round))
                .setDesiredHeight(600)
                .setIntent(bubbleIntent)
                .setAutoExpandBubble(true)
                .setSuppressNotification(true)
                .build();

        Notification notif = new NotificationCompat.Builder(context.getApplicationContext(), model.id)
                .setSmallIcon(R.drawable.task)
                .setContentIntent(bubbleIntent)
                .setShortcutId("STAR_REMINDER")
                .addPerson(chatPerson)
                .setBubbleMetadata(bubbleData)
                .setStyle(messagingStyle)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(soundUri)
                .setChannelId(model.id)
                .setColor(context.getApplicationContext().getResources().getColor(R.color.themeColor))
                .build();
        nm.notify(model.notifIdExists? model.notifId: AlarmManager.NOTIF_ID, notif);


//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//
//        }
//        else{
//            Notification notif = new NotificationCompat.Builder(context.getApplicationContext(), model.id)
//                    .setContentTitle(model.name)
//                    .setContentText("Should start now, It's " + timeString)
//                    .setSmallIcon(R.drawable.task)
//                    .setLargeIcon(largeIcon)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setOngoing(false)
//                    .setSound(soundUri)
//                    .setChannelId(model.id)
//                    .setColor(context.getApplicationContext().getResources().getColor(R.color.themeColor))
//                    .setAutoCancel(true)
//                    .build();
//            nm.notify(model.notifIdExists? model.notifId:AlarmManager.NOTIF_ID, notif);
//        }


        if(!model.notifIdExists){
           AlarmManager.NOTIF_ID++;
        }

        if(AlarmManager.speech == null){
            AlarmManager.speech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i == TextToSpeech.SUCCESS){
                        final int res = AlarmManager.speech.setLanguage(new Locale("en", "US"));
                        AlarmManager.speech.setPitch(0.25f);
                        AlarmManager.speech.setSpeechRate(1.5f);

                        if(res == TextToSpeech.LANG_MISSING_DATA || res==TextToSpeech.LANG_NOT_SUPPORTED){
                            AlarmManager.speech.setLanguage(Locale.ENGLISH);
                            AlarmManager.speech.setVoice(new Voice("eng-USA",Locale.ENGLISH, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_LOW,true, null));
                        }
                        else{
                            final Voice voice = new Voice("eng-USA",new Locale("en", "US"), Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH,true, null);
                            AlarmManager.speech.setVoice(voice);
                        }


                        AlarmManager.speech.speak("This is a Reminder From Star Tasks", TextToSpeech.QUEUE_ADD, null, null);
                        AlarmManager.speech.speak("Your Task '" + model.name + "' should start now.", TextToSpeech.QUEUE_ADD, null, null);
                        AlarmManager.speech.speak("It is " + timeString, TextToSpeech.QUEUE_ADD, null, null);
                        AlarmManager.speech.speak("Please, Do not forget your Task", TextToSpeech.QUEUE_ADD, null, null);
                    }
                }
            });
        }
        else{
            AlarmManager.speech.speak("This is a Reminder From Star,", TextToSpeech.QUEUE_ADD, null, null);
            AlarmManager.speech.speak("Your Task '" + model.name + "' in Star Tasks should start now.", TextToSpeech.QUEUE_ADD, null, null);
            AlarmManager.speech.speak("It is " + timeString, TextToSpeech.QUEUE_ADD, null, null);
            AlarmManager.speech.speak("Please, Do not forget your Task", TextToSpeech.QUEUE_ADD, null, null);
        }

    }

}