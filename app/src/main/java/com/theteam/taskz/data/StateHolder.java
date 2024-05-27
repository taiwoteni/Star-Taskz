package com.theteam.taskz.data;

import android.media.MediaPlayer;

import com.theteam.taskz.CalendarPageFragment;
import com.theteam.taskz.TasksPageFragment;

import java.util.List;

public class StateHolder {
    public static List<TasksPageFragment> taskPageFragments;
    public static List<CalendarPageFragment> calendarPageFragments;

    public static MediaPlayer mediaPlayer;
}
