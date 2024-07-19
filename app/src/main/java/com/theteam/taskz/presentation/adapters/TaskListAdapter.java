package com.theteam.taskz.presentation.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.CalendarItemModel;
import com.theteam.taskz.data.models.TaskModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

// Replace 'TaskDate' with your actual data class for task dates
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskItemViewHolder> {

    private List<CalendarItemModel> sortedCalendars;

    public TaskListAdapter(final List<CalendarItemModel> sortedCalendars) {
        this.sortedCalendars = sortedCalendars;
    }

    @NonNull
    @Override
    public TaskItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item_layout, parent, false); // Use your item layout
        return new TaskItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        final CalendarItemModel calendarItemModel = sortedCalendars.get(position);

        holder.bind(calendarItemModel);

    }

    public void setTasksList(List<CalendarItemModel> sortedCalendars){
        this.sortedCalendars = sortedCalendars;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sortedCalendars.size();
    }

    public static class TaskItemViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView textView;
        public LinearLayout tasksList;


        public TaskItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textView = (TextView) itemView.findViewById(R.id.time_text);
            tasksList = (LinearLayout) itemView.findViewById(R.id.tasks_list);
        }

        public void bind(CalendarItemModel calendarItemModel){
            final SimpleDateFormat dateFormat = new SimpleDateFormat("hh a", Locale.getDefault());

            textView.setText(dateFormat.format(calendarItemModel.date.getTime()).toUpperCase());


            for(final TaskModel task:calendarItemModel.tasks){
                final LayoutInflater inflater = (LayoutInflater) view.getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.tasks_item_card_layout, null, false);
                final ImageView taskType = (ImageView) layout.findViewById(R.id.taskType);
                final TextView taskName = (TextView) layout.findViewById(R.id.taskName);
                final TextView taskTime = (TextView) layout.findViewById(R.id.taskTimeRange);
                final LinearLayout taskLayout = (LinearLayout) layout.findViewById(R.id.task_item_layout);

                switch (task.category.toLowerCase()){
                    case "study":
                        taskLayout.setBackgroundResource(R.drawable.study_task_background);
                        break;
                    case "personal":
                        taskLayout.setBackgroundResource(R.drawable.personal_task_background);
                        break;
                    case "work":
                        taskLayout.setBackgroundResource(R.drawable.work_task_background);
                        break;
                    default:
                        taskLayout.setBackgroundResource(R.drawable.uncategorized_task_background);
                        break;
                }
                taskName.setText(task.name);
                taskType.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                taskTime.setText(timeFormat.format(task.startTime.getTime()).toUpperCase());

                tasksList.addView(layout);
            }
        }
    }
}
