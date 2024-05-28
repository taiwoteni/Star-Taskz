package com.theteam.taskz.home_pages;

import android.animation.Animator;
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

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theteam.taskz.CreateTask;
import com.theteam.taskz.LoginActivity;
import com.theteam.taskz.R;
import com.theteam.taskz.adapters.TaskPageAdapter;
import com.theteam.taskz.data.StateHolder;
import com.theteam.taskz.data.ViewPagerDataHolder;
import com.theteam.taskz.models.TaskManager;
import com.theteam.taskz.models.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TaskFragment extends Fragment {

    private TextView dayText,dateText,todayText;
    private ImageView categoryIcon,logoutIcon;
    private static LottieAnimationView confetti_lottie;

    private FloatingActionButton fab;

    private TaskPageAdapter adapter;
    private ArrayList<Calendar> dates;
    private Calendar currentDate;
    private ViewPager2 viewerPagerTasks;
    private SimpleDateFormat sdf, dayOfWeekFormat;
    private String dateString, dayOfWeekString;

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
        logoutIcon = view.findViewById(R.id.logout_icon);
        fab = view.findViewById(R.id.fab);
        confetti_lottie = (LottieAnimationView) view.findViewById(R.id.confetti_lottie);

        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TaskManager(requireActivity()).clearTasks();
                UserModel.clearUserData(requireActivity());
                Intent i = new Intent(requireActivity().getApplicationContext(), LoginActivity.class);
                requireActivity().startActivity(i);
                requireActivity().finish();


            }
        });

        confetti_lottie.setAnimation(R.raw.confetti);
        confetti_lottie.setRepeatCount(0);
        confetti_lottie.setSpeed(0.95f);
        confetti_lottie.setRepeatMode(LottieDrawable.RESTART);
        confetti_lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                stopConfetti();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });



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
        adapter.notifyDataSetChanged();
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
                StateHolder.currentIndex = position;

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
                StateHolder.currentIndex = position;

                // Now we also have to prevent users from adding new Tasks to
                // Days that have expired.

                fab.setVisibility(isPrevious(date)? View.GONE:View.VISIBLE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //We only want to refresh the page after the user has gone past the AI fragment
        if(ViewPagerDataHolder.viewPager != null && ViewPagerDataHolder.viewPager.getCurrentItem() > 2){
            dates = generateDates();
            int middleIndex = dates.size()/2;
            for(Calendar cal : dates){
                if(cal.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                    middleIndex = dates.indexOf(cal);
                }
            }

            adapter = new TaskPageAdapter(this, dates);
            viewerPagerTasks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            viewerPagerTasks.setCurrentItem(middleIndex, false);

        }


    }

    void showMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void stopConfetti(){
        confetti_lottie.setVisibility(View.GONE);
    }

    public static void startConfetti(){
        confetti_lottie.setVisibility(View.VISIBLE);
        confetti_lottie.playAnimation();
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
