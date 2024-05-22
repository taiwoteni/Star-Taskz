package com.theteam.taskz.utilities;

import android.content.Context;

import com.theteam.taskz.R;
import com.theteam.taskz.models.TaskModel;

public class ColorSwatch {

    private final Context context;
    public ColorSwatch(Context context){
        this.context = context;
    }
    public int getTaskColor(TaskModel model){
        switch (model.category.toLowerCase()){
            case "personal":
                return context.getApplicationContext().getColor(R.color.purple_700);
            case "uncategorized":
                return context.getApplicationContext().getColor(R.color.yellow);
            case "study":
                return context.getApplicationContext().getColor(R.color.themeColor);
            //This would be for "work"
            default:
                return context.getApplicationContext().getColor(R.color.red);
        }
    }
}
