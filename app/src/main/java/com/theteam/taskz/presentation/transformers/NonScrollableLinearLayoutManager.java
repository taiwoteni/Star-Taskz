package com.theteam.taskz.presentation.transformers;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

public class NonScrollableLinearLayoutManager extends LinearLayoutManager {

    public NonScrollableLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return false;  // Disable vertical scrolling
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;  // Disable horizontal scrolling if needed
    }
}


