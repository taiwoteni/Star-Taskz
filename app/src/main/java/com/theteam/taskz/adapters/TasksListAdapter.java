package com.theteam.taskz.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.theteam.taskz.CreateTask;
import com.theteam.taskz.R;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.enums.TaskStatus;
import com.theteam.taskz.utilities.ThemeManager;
import com.theteam.taskz.home_pages.TaskFragment;
import com.theteam.taskz.models.TaskModel;
import com.theteam.taskz.utilities.AlarmManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TasksListAdapter extends RecyclerView.Adapter<TasksListAdapter.Tasks_Page_ViewHolder> {
    private ArrayList<TaskModel> current_tasks;
    private Context context;
    private AlarmManager manager;

    public TasksListAdapter(Context context,ArrayList<TaskModel> current_tasks){
        this.current_tasks = current_tasks;
        this.context = context;
        manager = new AlarmManager(this.context.getApplicationContext(), this.context);
    }

    @NonNull
    @Override
    public Tasks_Page_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
        return new Tasks_Page_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Tasks_Page_ViewHolder holder, int position) {
        final ThemeManager theme = new ThemeManager(context);

        final TaskModel task = current_tasks.get(position);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        holder.nameTxt.setText(task.name);
        holder.timeText.setText(dateFormat.format(task.date.getTime()).toUpperCase());
        holder.categoryText.setText("# " + task.category);

        if(task.status == TaskStatus.Pending){
            holder.checkButton.setImageResource(R.drawable.check_outline);
            holder.checkButton.setColorFilter(theme.secondary);
            holder.timeText.setTextColor(theme.secondary);
            holder.timeText.setBackgroundTintList(ColorStateList.valueOf(theme.tertiary));
            holder.nameTxt.setPaintFlags(holder.nameTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        }
        else{
            holder.checkButton.setImageResource(R.drawable.check);
            holder.checkButton.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.themeColor));
            holder.timeText.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.timeText.setBackgroundTintList(ColorStateList.valueOf(holder.itemView.getContext().getResources().getColor(R.color.themeColor)));
            holder.nameTxt.setPaintFlags(holder.nameTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        final View.OnClickListener toggleStatus = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TaskManager taskManager = new TaskManager(holder.itemView.getContext());
                if(task.status == TaskStatus.Pending){
                    task.updateStatus(TaskStatus.Completed);
                    TaskFragment.startConfetti();
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
                    holder.checkButton.setColorFilter(theme.secondary);
                    holder.timeText.setTextColor(theme.secondary);
                    holder.timeText.setBackgroundTintList(ColorStateList.valueOf(theme.tertiary));
                    holder.nameTxt.setPaintFlags(holder.nameTxt.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                    if(Calendar.getInstance().toInstant().isBefore(task.date.toInstant())){
                        manager.setAlarm(task, false);

                    }

                }

            }
        };
        final View.OnLongClickListener viewPopup = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopupMenu(holder.timeText, position, holder);
                return true;
            }
        };

        holder.checkButton.setOnClickListener(toggleStatus);
        holder.itemView.setOnClickListener(toggleStatus);
        holder.itemView.setOnLongClickListener(viewPopup);


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

    void showPopupMenu(View v, int index, Tasks_Page_ViewHolder holder){
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.task_item_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                final TaskModel task = current_tasks.get(index);
                if(menuItem.getItemId() == R.id.delete){
                    if(Calendar.getInstance().toInstant().isBefore(task.date.toInstant())){
                        manager.cancelAlarm(task);
                    }
                    new TaskManager(context).deleteTask(task);
                }
                if(menuItem.getItemId() == R.id.edit){
                    context.startActivity(new Intent(context, CreateTask.class).putExtra("data", new Gson().toJson(task.toJson())));
                }

                return true;
            }
        });
        popupMenu.show();

    }

}
