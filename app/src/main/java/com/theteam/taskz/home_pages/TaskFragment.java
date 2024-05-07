package com.theteam.taskz.home_pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theteam.taskz.CreateTask;
import com.theteam.taskz.R;
import com.theteam.taskz.TaskPageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TaskFragment extends Fragment {

    private TextView dayText,dateText,todayText;
    private ImageView categoryIcon;

    private FloatingActionButton fab;

    TaskPageAdapter adapter;
    ArrayList<Calendar> dates;
    Calendar currentDate;
    ViewPager2 viewerPagerTasks;
    SimpleDateFormat sdf, dayOfWeekFormat;
    String dateString, dayOfWeekString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tasks_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewerPagerTasks = view.findViewById(R.id.view_pager);
        dayText = view.findViewById(R.id.day_text);
        dateText = view.findViewById(R.id.date_text);
        todayText = view.findViewById(R.id.today_text);
        categoryIcon = view.findViewById(R.id.category_icon);
        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(view1 -> {
//            createTaskView = new CreateTaskView(TaskFragment.this);
//            createTaskView.setCancelable(false);
//            createTaskView.show(getParentFragmentManager(), createTaskView.getTag());



            Intent intent = new Intent(getActivity().getApplicationContext(), CreateTask.class);
            startActivity(intent);
        });

        // This will show the list of categories to be displayed inorder to filter tasks



        dates = generateDates();
        int middleIndex = dates.size()/2;
        for(Calendar cal : dates){
            if(cal.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                middleIndex = dates.indexOf(cal);
            }
        }

        adapter = new TaskPageAdapter(this, dates);
        viewerPagerTasks.setAdapter(adapter);
        viewerPagerTasks.setCurrentItem(middleIndex, false);

        currentDate = dates.get(middleIndex);
        sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        dateString = sdf.format(currentDate.getTime());
        dateText.setText(dateString);

        dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        dayOfWeekString = dayOfWeekFormat.format(currentDate.getTime());
        dayText.setText(dayOfWeekString);

        todayText.setVisibility(isToday(currentDate)? View.VISIBLE:View.GONE);

        viewerPagerTasks.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                // Update dateInTheWeek TextView when page is selected
                currentDate = dates.get(position);
                Calendar date = dates.get(position);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
                String dateString = sdf.format(date.getTime());
                dateText.setText(dateString);

                SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                String dayOfWeekString = dayOfWeekFormat.format(date.getTime());
                dayText.setText(dayOfWeekString);

                todayText.setVisibility(isToday(date)? View.VISIBLE:View.GONE);

                // Now we also have to prevent users from adding new Tasks to
                // Days that have expired.

                fab.setVisibility(isPrevious(date)? View.GONE:View.VISIBLE);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Update dateInTheWeek TextView when page is selected
                currentDate = dates.get(position);
                Calendar date = dates.get(position);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
                String dateString = sdf.format(date.getTime());
                dateText.setText(dateString);

                SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                String dayOfWeekString = dayOfWeekFormat.format(date.getTime());
                dayText.setText(dayOfWeekString);

                todayText.setVisibility(isToday(date)? View.VISIBLE:View.GONE);

                // Now we also have to prevent users from adding new Tasks to
                // Days that have expired.

                fab.setVisibility(isPrevious(date)? View.GONE:View.VISIBLE);
            }
        });

    }



    void showMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<Calendar> generateDates() {
        ArrayList<Calendar> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = -30; i <= 30; i++) {
            Calendar date = (Calendar) calendar.clone();
            date.add(Calendar.DAY_OF_YEAR, i);
            dates.add(date);
        }

        return dates;
    }

    boolean isToday(final Calendar currentDate){
        final Calendar live = Calendar.getInstance();
        final int liveDay = live.get(Calendar.DAY_OF_MONTH);
        final int liveMonth = live.get(Calendar.MONTH);

        final  int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        final int currentMonth = currentDate.get(Calendar.MONTH);

        return liveDay==currentDay && liveMonth == currentMonth;

    }

    boolean isPrevious(final Calendar currentDate){
        final Calendar live = Calendar.getInstance();
        final int liveDay = live.get(Calendar.DAY_OF_MONTH);
        final int liveMonth = live.get(Calendar.MONTH);

        final  int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        final int currentMonth = currentDate.get(Calendar.MONTH);

        return liveMonth>currentMonth || (currentDay<liveDay);
    }
}
