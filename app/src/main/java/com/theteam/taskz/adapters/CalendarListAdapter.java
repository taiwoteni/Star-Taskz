package com.theteam.taskz.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.theteam.taskz.models.CalendarItemModel;
import com.theteam.taskz.utilities.ColorSwatch;
import com.theteam.taskz.R;
import com.theteam.taskz.enums.TaskStatus;
import com.theteam.taskz.models.TaskModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CalendarListAdapter extends BaseAdapter {

    private ArrayList<CalendarItemModel> items;
    private Context context;


    public CalendarListAdapter(Context context,ArrayList<CalendarItemModel> items){
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public CalendarItemModel getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final CalendarItemModel model = items.get(i);
        View calendarView = view;
        if(calendarView == null){
            calendarView = inflater.inflate(R.layout.calendar_list_item_layout, viewGroup,false);
        }
        final TextView time_text = (TextView) calendarView.findViewById(R.id.time_text);
        final LinearLayout taskListLayoutRoot = calendarView.findViewById(R.id.tasks_list_layout_root);
        final LinearLayout taskListLayout = taskListLayoutRoot.findViewById(R.id.tasks_list_layout);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH a", Locale.getDefault());
        time_text.setText(dateFormat.format(model.date.getTime()).toUpperCase());
        taskListLayout.removeAllViews();

        if(model.tasks.isEmpty()){
            final View taskListItemView = inflater.inflate(R.layout.calendar_task_list_empty_layout_item, taskListLayout, false);
            taskListLayout.addView(taskListItemView);
        }
        else {
            for(final TaskModel task : model.tasks){
                final View taskListItemView = inflater.inflate(R.layout.calendar_task_list_layout_item, taskListLayout, false);
                final TextView taskName = taskListItemView.findViewById(R.id.task_name);
                final TextView taskCategory = taskListItemView.findViewById(R.id.task_category);

                taskName.setText(task.name);
                taskCategory.setText("#" + task.category);

                if(task.status == TaskStatus.Completed){
                    taskName.setPaintFlags(taskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                taskListItemView.setBackgroundTintList(ColorStateList.valueOf(new ColorSwatch(context).getTaskColor(task)));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, // width
                        ViewGroup.LayoutParams.MATCH_PARENT // height
                );
                boolean isUpTo3 = model.tasks.size()>=3;
                if(model.tasks.indexOf(task) != 0){
                    layoutParams.setMarginStart(isUpTo3?3:context.getResources().getDimensionPixelSize(R.dimen.small_value_size));
                }
                if(model.tasks.indexOf(task) != model.tasks.size()-1){
                    layoutParams.setMarginEnd(isUpTo3?3:context.getResources().getDimensionPixelSize(R.dimen.small_value_size));
                }
                if(isUpTo3){
                    taskName.setTextSize(TextView.AUTO_SIZE_TEXT_TYPE_NONE, 34f);
                    taskCategory.setTextSize(TextView.AUTO_SIZE_TEXT_TYPE_NONE,25f);
                }

                layoutParams.weight = 1;
                taskListItemView.setLayoutParams(layoutParams);

                taskListLayout.addView(taskListItemView);

            }
        }

        return calendarView;
    }
    void showMessage(final String message){
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
