package com.theteam.taskz.presentation.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.theteam.taskz.domain.entities.Task;
import com.theteam.taskz.domain.repositories.TaskRepository;
import com.theteam.taskz.utils.enums.TaskStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

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

    @Override
    public long getItemId(int position) {
        return position;
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
            tasksList.removeAllViews();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("hh a", Locale.getDefault());

            textView.setText(dateFormat.format(calendarItemModel.date.getTime()).toUpperCase());


            for(final Task task:calendarItemModel.tasks){
                final LayoutInflater inflater = (LayoutInflater) view.getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.tasks_item_card_layout, null, false);
                final ImageView clock = (ImageView) layout.findViewById(R.id.clock);
                final ImageView dot = (ImageView) layout.findViewById(R.id.dot);
                final TextView taskProgress = layout.findViewById(R.id.task_progress);
                final TextView taskStatus = layout.findViewById(R.id.taskStatus);
                final ImageView more = layout.findViewById(R.id.more);
                final TextView taskName = (TextView) layout.findViewById(R.id.taskName);
                final TextView taskTime = (TextView) layout.findViewById(R.id.taskTimeRange);
                final LinearLayout collaborators_list = layout.findViewById(R.id.collaborators_list);
                final LinearLayout taskLayout = (LinearLayout) layout.findViewById(R.id.task_item_layout);

                switch (task.taskCategory().toLowerCase()){
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

                taskName.setText(task.taskName());
                taskStatus.setText(task.taskStatus().name());
                clock.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                more.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                dot.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                if (task.taskHasSteps()){
                    taskProgress.setText(task.taskProgress() + "%");
                }else {
                    taskProgress.setVisibility(View.GONE);
                    dot.setVisibility(View.GONE);
                }

                final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                taskTime.setText("Starting " + timeFormat.format(task.startTime().getTime()).toUpperCase());

                final int day = calendarItemModel.date.get(Calendar.DAY_OF_YEAR);
                final int start = task.startTime().get(Calendar.DAY_OF_YEAR);

                if(task.hasDeadline()){
                    final int deadlineDay = task.dueTime().get(Calendar.DAY_OF_YEAR);
                    if(start!=day && deadlineDay>day){
                        taskTime.setText("All Day");
                    }else if (deadlineDay == day){
                        taskTime.setText("Due " + timeFormat.format(task.dueTime().getTime()).toUpperCase());


                    }

                }



                if (task.completed()){
                    taskTime.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                    taskName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                    taskStatus.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                }

                if(task.isCollaborating()){
                    for (int i = 0; i<task.assignees().size(); i++){
                        CircleImageView pr = (CircleImageView) inflater.inflate(R.layout.collaborator_icon, null);

                        collaborators_list.addView(pr);

                    }
                }

                layout.setOnClickListener(view1 -> {
                    new TaskRepository(view.getContext()).updateTaskStatus(
                            task.taskId(),
                            task.completed()?TaskStatus.Pending:TaskStatus.Completed,
                            null,
                            jsonObject -> {

                            },
                            volleyError -> {

                            }
                    );
                });

                tasksList.addView(layout);
            }
        }
    }
}
