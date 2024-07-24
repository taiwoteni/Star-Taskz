package com.theteam.taskz.data.models;

import com.theteam.taskz.domain.entities.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarItemModel {
    public ArrayList<Task> tasks;

   public  Calendar date;

    public CalendarItemModel(final ArrayList<Task> tasks, Calendar date){
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

    public void addTask(Task e){
        tasks.add(e);
    }
    public  void setTasks(ArrayList<Task> tasks){
        this.tasks = tasks;
    }

}
