package com.theteam.taskz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarPageFragment extends Fragment {
    public Calendar date;

    private CalendarListAdapter calendarListAdapter;
    private ListView calendarListView;

    private int lastContainedIndex = 0;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<TaskModel> tasks;
    private ArrayList<TaskModel> all_tasks;
    private TaskStatus status = null;


    public CalendarPageFragment(){
        date = Calendar.getInstance();
    }

    public CalendarPageFragment(Calendar date){
        this.date = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_page_item_layout, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        calendarListView = (ListView) view.findViewById(R.id.calendar_page_item_list);


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

    void showMessage(final String message){
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void instantiateTasks(){
        // We get all the saved tasks on the device.
        ArrayList<TaskModel> offlineTasks = new TaskManager(requireActivity()).getTasks();

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

        final ArrayList<CalendarItemModel> calendarModels = generateModels();
        calendarListAdapter = new CalendarListAdapter(getContext(),calendarModels);
        calendarListView.setAdapter(calendarListAdapter);
        calendarListAdapter.notifyDataSetChanged();

        calendarListView.setSelection(lastContainedIndex);


    }

    private ArrayList<CalendarItemModel> generateModels(){
        final ArrayList<CalendarItemModel> models = new ArrayList<>();

        for(int i = 0; i<=23; i++){
            final CalendarItemModel model = new CalendarItemModel();

            // We first set the calendar's hour and minute to Hour:00
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, i);
            calendar.set(Calendar.MINUTE, 0);
            model.setDate(calendar);

            // Then we get the tasks that belong to this particular hour
            for(final TaskModel task : this.tasks){
                if(task.date.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY)){
                    model.addTask(task);
                    lastContainedIndex = i;
                }
            }
            models.add(model);
        }

        return models;
    }
}
