package com.theteam.taskz.presentation.transformers;
import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;

import com.theteam.taskz.presentation.views.TasksFragment;

public class CustomLinearSmoothScroller extends LinearSmoothScroller {

    public CustomLinearSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected float calculateSpeedPerPixel(@NonNull DisplayMetrics displayMetrics) {
        return TasksFragment.MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }
}
