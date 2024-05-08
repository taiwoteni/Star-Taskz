package com.theteam.taskz;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.Tasks_Page_ViewHolder> {
    private ArrayList<TaskModel> current_tasks;
    private Context context;

    public TasksListAdapter(Context context,ArrayList<TaskModel> current_tasks){
        this.current_tasks = current_tasks;
        this.context = context;
    }

    @NonNull
    @Override
    public Tasks_Page_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return new Tasks_Page_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Tasks_Page_ViewHolder holder, int position) {
        final TaskModel task = current_tasks.get(position);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(":mm a", Locale.getDefault());
        holder.nameTxt.setText(task.name);
        holder.timeText.setText(task.date.get(Calendar.HOUR)+dateFormat.format(task.date.getTime()).toUpperCase());
        holder.categoryText.setText("# " + task.category);

        if(task.status == TaskStatus.Pending){
            holder.checkButton.setImageResource(R.drawable.check_outline);
            holder.checkButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.secondaryLight));
            holder.timeText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.secondaryLight));
            holder.timeText.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.tertiaryPrimary)));
            holder.nameTxt.setPaintFlags(holder.nameTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        }
        else{
            holder.checkButton.setImageResource(R.drawable.check);
            holder.checkButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.themeColor));
            holder.timeText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.timeText.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.themeColor)));
            holder.nameTxt.setPaintFlags(holder.nameTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        final AlarmManager manager = new AlarmManager(context.getApplicationContext(), context);
        final View.OnClickListener toggleStatus = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TaskManager taskManager = new TaskManager(holder.itemView.getContext());
                if(task.status == TaskStatus.Pending){
                    task.updateStatus(TaskStatus.Completed);
                    taskManager.updateTask(task);
                    holder.checkButton.setImageResource(R.drawable.check);
                    holder.checkButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.themeColor));
                    holder.timeText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
                    holder.timeText.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.themeColor)));
                    holder.nameTxt.setPaintFlags(holder.nameTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    if(Calendar.getInstance().toInstant().isBefore(task.date.toInstant())){
                        manager.cancelAlarm(task);
                    }
                }
                else{
                    task.updateStatus(TaskStatus.Pending);
                    taskManager.updateTask(task);
                    holder.checkButton.setImageResource(R.drawable.check_outline);
                    holder.checkButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.secondaryLight));
                    holder.timeText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.secondaryLight));
                    holder.timeText.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.tertiaryPrimary)));
                    holder.nameTxt.setPaintFlags(holder.nameTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                    if(Calendar.getInstance().toInstant().isBefore(task.date.toInstant())){
                        manager.setAlarm(task);

                    }

                }

            }
        };

        holder.checkButton.setOnClickListener(toggleStatus);
        holder.itemView.setOnClickListener(toggleStatus);


        if(position == 0){
            holder.divider.setVisibility(View.INVISIBLE);
        }

//        holder.ageTxt.setText(tasks_page_models.get(position).getAge());
    }

    @Override
    public int getItemCount() {
        return current_tasks.size();
    }

    public class Tasks_Page_ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt, categoryText, placeTxt, timeText;

        View itemView;

        ImageView checkButton;
        LinearLayout divider;

        public Tasks_Page_ViewHolder(@NonNull View view) {
            super(view);

            itemView = view;
            timeText = itemView.findViewById(R.id.timeText);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            categoryText = itemView.findViewById(R.id.ageTxt);
            placeTxt = itemView.findViewById(R.id.placeTxt);
            divider = itemView.findViewById(R.id.divider);
            checkButton = itemView.findViewById(R.id.checkBtn);
        }
    }
    void showMessage(final String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
