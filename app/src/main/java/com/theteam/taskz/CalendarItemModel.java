package com.theteam.taskz;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarItemModel {
    ArrayList<TaskModel> tasks;

    Calendar date;

    public CalendarItemModel(final ArrayList<TaskModel> tasks, Calendar date){
        this.tasks = tasks;
        this.date = date;
    }

    public CalendarItemModel(){
        tasks = new ArrayList<>();
        date = Calendar.getInstance();
    }

    public void setDate(Calendar calendar){
        date = calendar;
    }

    public void addTask(TaskModel e){
        tasks.add(e);
    }
    public  void setTasks(ArrayList<TaskModel> tasks){
        this.tasks = tasks;
    }

}
