package com.theteam.taskz;

import android.content.Context;
import android.graphics.Color;

public class ColorSwatch {

    Context context;
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
