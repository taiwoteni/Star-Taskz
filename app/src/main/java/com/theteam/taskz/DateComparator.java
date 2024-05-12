package com.theteam.taskz;

import java.util.Comparator;

public class DateComparator implements Comparator<TaskModel> {
    @Override
    public int compare(TaskModel obj1, TaskModel obj2) {
        return obj1.date.getTime().compareTo(obj2.date.getTime());
    }
}
