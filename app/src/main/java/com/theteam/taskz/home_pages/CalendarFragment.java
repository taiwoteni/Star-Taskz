package com.theteam.taskz.home_pages;

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

import com.theteam.taskz.adapters.CalendarPageAdapter;
import com.theteam.taskz.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private TextView dayText,dateText,todayText;
    private ImageView categoryIcon;
    CalendarPageAdapter calendarPageAdapter;
    ArrayList<Calendar> dates;
    Calendar currentDate;
    ViewPager2 viewerPagerCalendars;
    SimpleDateFormat sdf, dayOfWeekFormat;
    String dateString, dayOfWeekString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewerPagerCalendars = view.findViewById(R.id.view_pager);
        dayText = view.findViewById(R.id.day_text);
        dateText = view.findViewById(R.id.date_text);
        todayText = view.findViewById(R.id.today_text);
        categoryIcon = view.findViewById(R.id.category_icon);

        dates = generateDates();
        int middleIndex = dates.size()/2;
        for(Calendar cal : dates){
            if(cal.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                middleIndex = dates.indexOf(cal);
            }
        }

        calendarPageAdapter = new CalendarPageAdapter(this, dates);
        viewerPagerCalendars.setAdapter(calendarPageAdapter);
        viewerPagerCalendars.setCurrentItem(middleIndex, false);

        currentDate = dates.get(middleIndex);
        sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        dateString = sdf.format(currentDate.getTime());
        dateText.setText(dateString);

        dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        dayOfWeekString = dayOfWeekFormat.format(currentDate.getTime());
        dayText.setText(dayOfWeekString);

        todayText.setVisibility(isToday(currentDate)? View.VISIBLE:View.GONE);

        viewerPagerCalendars.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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
}
