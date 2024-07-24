package com.theteam.taskz.broadcasts;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theteam.taskz.data.models.StateHolder;
import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.utils.enums.TaskStatus;
import com.theteam.taskz.data.models.TaskManager;
import com.theteam.taskz.data.models.TaskModel;
import com.theteam.taskz.utils.others.AlarmManager;

import java.util.HashMap;

public class TaskNotifReceiver extends BroadcastReceiver {

    public static String ACTION_COMPLETED = "com.theteam.taskz.COMPLETED";
    public static String ACTION_PENDING = "com.theteam.taskz.PENDING";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        final HashMap<String,Object> taskJson = new Gson().fromJson(bundle.getString("TASK"), new TypeToken<HashMap<String,Object>>(){}.getType());
        final Task task = Task.fromJson(taskJson);
        NotificationManager nm = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(task.taskLocalId());

        if(StateHolder.mediaPlayer != null){
            StateHolder.mediaPlayer.stop();
        }

        final TaskManager manager = new TaskManager(context.getApplicationContext());
//        manager.updateStatus(intent.getAction().equalsIgnoreCase(ACTION_PENDING)?TaskStatus.Pending:TaskStatus.Completed);
//        manager.updateTask(model);
        AlarmManager alarm = new AlarmManager(context,context.getApplicationContext());
        alarm.cancelAlarm(task);
        alarm.cancelAlarm(task);




    }
}