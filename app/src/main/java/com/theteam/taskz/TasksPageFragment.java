package com.theteam.taskz;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TasksPageFragment extends Fragment {
    private Calendar date;
    private TextView noTasksText, allText, pendingText, completedText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<TaskModel> tasks;
    private ArrayList<TaskModel> all_tasks;
    private TaskStatus status = null;

    private LinearProgressIndicator tasks_progress;
    private TextView progress_text;

    LinearLayoutManager linearLayoutManager;

    TasksListAdapter tasksListAdapter;

    RecyclerView taskRecyclerView;
    LinearLayout noTaskLayout, taskLayout, allLayout,pendingLayout,completedLayout;

    public TasksPageFragment(){
        date = Calendar.getInstance();
    }

    public TasksPageFragment(Calendar date){
        this.date = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_page_item_layout, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskRecyclerView = view.findViewById(R.id.taskRecyclerView);
        noTaskLayout = view.findViewById(R.id.noTaskLayout);
        noTasksText = view.findViewById(R.id.no_tasks_text);
        taskLayout = view.findViewById(R.id.taskLayout);
        allLayout = view.findViewById(R.id.all_layout);
        pendingLayout = view.findViewById(R.id.pending_layout);
        completedLayout = view.findViewById(R.id.completed_layout);
        allText = view.findViewById(R.id.all_text);
        pendingText = view.findViewById(R.id.pending_text);
        completedText = view.findViewById(R.id.completed_text);
        progress_text = view.findViewById(R.id.progress_text);
        tasks_progress = view.findViewById(R.id.task_progress);
        tasks_progress.setProgress(0);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, true);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        linearLayoutManager.setStackFromEnd(true);
        taskRecyclerView.setLayoutManager(linearLayoutManager);

        allLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshButtonColors();
                status = null;
                instantiateTasks();
                allLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                allText.setTextColor(getResources().getColor(R.color.white));
            }
        });
        pendingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshButtonColors();
                status = TaskStatus.Pending;
                instantiateTasks();
                pendingLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                pendingText.setTextColor(getResources().getColor(R.color.white));
            }
        });
        completedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshButtonColors();
                status = TaskStatus.Completed;
                instantiateTasks();
                completedLayout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
                completedText.setTextColor(getResources().getColor(R.color.white));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        instantiateTasks();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);

            }
        });
        instantiateTasks();
    }



    boolean isToday(final Calendar currentDate){
        final Calendar live = Calendar.getInstance();
        final int liveDay = live.get(Calendar.DAY_OF_MONTH);
        final int liveMonth = live.get(Calendar.MONTH);

        final  int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        final int currentMonth = currentDate.get(Calendar.MONTH);

        return liveDay==currentDay && liveMonth == currentMonth;

    }

    void refreshButtonColors(){
        final ThemeManager theme = new ThemeManager(requireActivity());
        allLayout.setBackgroundTintList(ColorStateList.valueOf(theme.tertiary));
        allText.setTextColor(theme.secondary);
        pendingLayout.setBackgroundTintList(ColorStateList.valueOf(theme.tertiary));
        pendingText.setTextColor(theme.secondary);
        completedLayout.setBackgroundTintList(ColorStateList.valueOf(theme.tertiary));
        completedText.setTextColor(theme.secondary);
    }

    void showMessage(final String message){
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    void instantiateTasks(){
        // We get all the saved tasks on the device.
        ArrayList<TaskModel> offlineTasks = new TaskManager(getContext()).getTasks();

//        showMessage(String.valueOf(offlineTasks.size()));

        tasks = new ArrayList<>();
        all_tasks = new ArrayList<>();

        // Then we filter the dates to show only if the days for a task match the current day
        for(final TaskModel task : offlineTasks){
            if(task.date.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)){
                if(status == null){
                    tasks.add(task);
                }
                else{
                    if(task.status == status){
                        tasks.add(task);
                    }
                }

            }
        }
        for(final TaskModel task : offlineTasks){
            if(task.date.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)){
                all_tasks.add(task);
            }
        }

        tasks_progress.setMax(all_tasks.size());

        int completed = 0;
        for(final TaskModel task : all_tasks){
            if(task.status == TaskStatus.Completed){
                completed+=1;
            }
        }

        tasks_progress.setProgress(completed,true);
        double percentage = ((double) completed /all_tasks.size()) * 100;
        progress_text.setText(Math.round(percentage) + "% Completed" + (percentage == 100? " ✨":""));

        tasksListAdapter = new TasksListAdapter(getActivity(),tasks);
        taskRecyclerView.setAdapter(tasksListAdapter);
        tasksListAdapter.notifyDataSetChanged();

        if (!all_tasks.isEmpty()){
            taskLayout.setVisibility(View.VISIBLE);
            noTaskLayout.setVisibility(tasks.isEmpty()?View.VISIBLE:View.GONE);
        }
        else {
            final DateFormat format = new SimpleDateFormat("MMM dd", Locale.getDefault());
            final String dateText = isToday(date)? "Today": "on " + format.format(date.getTime());
            noTaskLayout.setVisibility(View.VISIBLE);
            noTasksText.setText("No Tasks " + dateText);
            taskLayout.setVisibility(View.GONE);
        }
        if(tasks.isEmpty() && !all_tasks.isEmpty()){
            noTasksText.setText("No " + status.name() + " tasks");
        }
    }
}
