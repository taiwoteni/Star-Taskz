package com.theteam.taskz.utils.others;

import com.theteam.taskz.data.models.TaskModel;

import java.util.Comparator;

public class DateComparator implements Comparator<TaskModel> {
    @Override
    public int compare(TaskModel obj1, TaskModel obj2) {
        return obj1.startTime.getTime().compareTo(obj2.startTime.getTime());
    }
}
