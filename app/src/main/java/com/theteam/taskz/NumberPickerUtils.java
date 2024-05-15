package com.theteam.taskz;


import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.View;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

public class NumberPickerUtils {

    public static void setDividerColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();

        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            try {
                Field dividerField = numberPicker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
                dividerField.set(child, null);
                child.setBackgroundColor(color);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
