package com.theteam.taskz.presentation.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.CalendarItemModel;
import com.theteam.taskz.data.models.TaskDateModel;
import com.theteam.taskz.data.models.TaskModel;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.presentation.adapters.TaskListAdapter;
import com.theteam.taskz.presentation.transformers.NonScrollableLinearLayoutManager;
import com.theteam.taskz.presentation.viewmodels.TaskDatesViewModel;
import com.theteam.taskz.presentation.viewmodels.TasksViewModel;
import com.theteam.taskz.utils.enums.AccountType;
import com.theteam.taskz.utils.others.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TasksPageFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout noTasksLayout;
    private LottieAnimationView noTasksLottie;
    private TextView taskDay,noTasksText;
    private LinearLayoutManager layoutManager;
    private TaskDatesViewModel taskDatesViewModel;
    private TaskDateModel taskDateModel;
    private TasksViewModel tasksViewModel;
    private TaskListAdapter adapter = null;

    private AccountType accountType;

    public TasksPageFragment(){
        taskDateModel = new TaskDateModel(Calendar.getInstance());
    }

    public TasksPageFragment(final TaskDateModel taskDateModel){
        this.taskDateModel = taskDateModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tasks_page_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountType = new UserModel(requireActivity()).accountType();

        recyclerView = (RecyclerView) view.findViewById(R.id.tasksList);
        taskDay = (TextView) view.findViewById(R.id.tasksDay);
        noTasksLayout = (LinearLayout) view.findViewById(R.id.noTaskLayout);
        noTasksText = (TextView) view.findViewById(R.id.no_tasks_text);
        noTasksLottie = (LottieAnimationView) view.findViewById(R.id.no_tasks_lottie);


        // Set the date on the day's text
        final String taskType = (accountType==AccountType.Business? "Projects":"Tasks");
        if(taskDateModel.isToday()){
            taskDay.setText("Today");
            noTasksText.setText("No " +taskType +" Today");

        }
        else{
            final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
            taskDay.setText(dateFormat.format(taskDateModel.getCalendar().getTime()));
            noTasksText.setText("No " + taskType + " on" + dateFormat.format(taskDateModel.getCalendar().getTime()));
        }


        // Configure recycler view
        layoutManager = new NonScrollableLinearLayoutManager(requireActivity());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setVerticalScrollBarEnabled(false);

        // Configure and consume tasksViewModelProvider
        tasksViewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        tasksViewModel.loadCachedTasks(requireActivity().getApplicationContext());

        initializeTasks();

    }

    private void initializeTasks(){
        final ArrayList<CalendarItemModel> sortedCalendars = generateSortedTasks(tasksViewModel.getTasks().getValue());
        adapter = new TaskListAdapter(sortedCalendars);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Log.v("TASKS_FRAGMENT","Sorted Calendar Size is " + sortedCalendars.size());

        if(!tasksViewModel.getTasks().hasObservers()){
            observeTask();
        }
    }

    private void observeTask(){
        tasksViewModel.getTasks().observe(getViewLifecycleOwner(), taskModels -> {
            final ArrayList<CalendarItemModel> sortedCalendars = generateSortedTasks(taskModels);
            Log.v("TASKS_FRAGMENT","Sorted Calendar Size in observer is " + sortedCalendars.size());

            adapter.setTasksList(sortedCalendars);
            adapter.notifyDataSetChanged();

        });
    }


    private ArrayList<CalendarItemModel> generateSortedTasks(final ArrayList<TaskModel> allTasks){
        /// We first of all sort out tasks that only occur on this particular day.
        final ArrayList<TaskModel> tasks = new ArrayList<>();
        for(final TaskModel task:allTasks){
            if(task.startTime.get(Calendar.DAY_OF_YEAR) == taskDateModel.getCalendar().get(Calendar.DAY_OF_YEAR)){
                tasks.add(task);
            }
        }


        final ArrayList<CalendarItemModel> calendarItemModels = new ArrayList<>();
        for(int i = 0; i<24; i++){
            final Calendar calendar = (Calendar) taskDateModel.getCalendar().clone();
            calendar.set(Calendar.HOUR_OF_DAY, i);
            final ArrayList<TaskModel> sortedTasks = new ArrayList<>();
            for(final TaskModel model: tasks){
                final boolean sameHour = model.startTime.get(Calendar.HOUR_OF_DAY) == i;
                final boolean sameDay = model.startTime.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);

                if(sameDay && sameHour){
                    sortedTasks.add(model);
                }
            }
            if(!sortedTasks.isEmpty()){
                calendarItemModels.add(new CalendarItemModel(sortedTasks, calendar));
            }
        }

        if(calendarItemModels.isEmpty()){
            noTasksLottie.setAnimation(new ThemeManager(requireActivity()).isDarkMode()? R.raw.empty_tasks_dark:R.raw.empty_tasks);
            noTasksLottie.loop(true);
            noTasksLottie.playAnimation();
            taskDay.setVisibility(View.GONE);
            noTasksLayout.setVisibility(View.VISIBLE);
        }
        else{
            if(noTasksLottie.isAnimating()){
                noTasksLottie.cancelAnimation();
            }
            noTasksLayout.setVisibility(View.GONE);
            taskDay.setVisibility(View.VISIBLE);

        }

        return calendarItemModels;
    }
}
