package com.theteam.taskz.presentation.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.TaskDateModel;
import com.theteam.taskz.presentation.viewmodels.TaskDatesViewModel;
import com.theteam.taskz.utils.others.ThemeManager;

import java.util.Calendar;
import java.util.List;

// Replace 'TaskDate' with your actual data class for task dates
public class TaskDateListAdapter extends RecyclerView.Adapter<TaskDateListAdapter.TaskDateViewHolder> {

    public List<TaskDateModel> taskDates;
    private TaskDatesViewModel taskDatesViewModel;
    private FragmentActivity context;

    public TaskDateListAdapter(List<TaskDateModel> taskDates, FragmentActivity context) {
        this.taskDates = taskDates;
        this.context = context;
        taskDatesViewModel = new ViewModelProvider(context).get(TaskDatesViewModel.class);
    }

    @NonNull
    @Override
    public TaskDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_date_item_layout, parent, false); // Use your item layout
        return new TaskDateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskDateViewHolder holder, int position) {
        final TaskDateModel taskDate = taskDates.get(holder.getAdapterPosition());
        final boolean isToday = taskDate.isToday();
        holder.dayText.setText(taskDate.day());
        holder.dayOfWeek.setText(taskDate.dayOfWeek());

        /// We initially set the indicator to only be visible if it's today
        /// We also set the selected holder to this holder if the selected holder is null
        /// and if the day is today. (Meaning, at the start, the currentHolder is usually today by default)
        holder.indicator.setVisibility(isToday? View.VISIBLE: View.INVISIBLE);
        if(taskDatesViewModel.getSelectedView().getValue() == null && isToday){
            taskDatesViewModel.setSelectedDate(taskDate.getCalendar());
            taskDatesViewModel.setSelectedView(holder);
        }

        taskDatesViewModel.getSelectedView().observe(context, taskDateViewHolder -> {
            /// We listen to check if the selectedView is this currentView
            /// Rather than always setting it programmatically.
            /// We are not animating back to normal, as calling this observer
            /// Applies to every holder in the RecyclerView and will lead to performance issues
            /// When we animate any(all) holders that are not selected

            if(taskDateViewHolder == holder){
                taskDatesViewModel.setSelectedDate(taskDate.getCalendar());
                selectHolder(holder);
            }else{
                deselectHolder(holder, false);
            }
        });


        holder.linearLayout.setOnClickListener(view -> {
            /// Before we make this holder selected in the provider,
            /// We, animate the current selected view (Which is soon going to be changed by this view),
            /// Back to a small size.

            TaskDateViewHolder selectedHolder = taskDatesViewModel.getSelectedView().getValue();
            if(selectedHolder != null){
                deselectHolder(selectedHolder, true);
            }
            taskDatesViewModel.setPreviousView(selectedHolder);
            taskDatesViewModel.setSelectedDate(taskDate.getCalendar());
            taskDatesViewModel.setSelectedView(holder);
        });
    }

    private void selectHolder(TaskDateViewHolder holder){
        holder.linearLayout.setBackgroundColor(holder.view.getResources().getColor(R.color.themeColor));
        holder.dayText.setTextColor(holder.view.getResources().getColor(R.color.white));
        holder.dayOfWeek.setTextColor(holder.view.getResources().getColor(R.color.secondaryDark));
        animateViewPadding(holder.linearLayout, dpToPx(14),dpToPx(18), dpToPx(10), dpToPx(16));
        holder.indicator.setColorFilter(Color.WHITE);
    }
    private void deselectHolder(TaskDateViewHolder holder, boolean shrink){
        /// Naturally, the shrink method should be both shrinking and changing colors.
        /// But based on the usecases, when it's shrinked, it's not followed up by changing the color back to white.
        /// and vice-versa. So with this, it's understandable to use this method.
        if(shrink){
            animateViewPadding(holder.linearLayout, dpToPx(18),dpToPx(14), dpToPx(16), dpToPx(10));
            return;
        }
        final boolean isDarkMode = new ThemeManager(context).isDarkMode();
        holder.indicator.setColorFilter(holder.view.getResources().getColor(R.color.themeColor));
        holder.linearLayout.setBackgroundColor(holder.view.getResources().getColor(isDarkMode? R.color.rootBackgroundDark:R.color.rootBackgroundLight));
        holder.dayText.setTextColor( holder.view.getResources().getColor(isDarkMode? R.color.primaryDark:R.color.primaryLight));
        holder.dayOfWeek.setTextColor(holder.view.getResources().getColor(isDarkMode?R.color.secondaryDark: R.color.secondaryLight));
    }

    static public void animateViewPadding(final View view, int startHorzontal, int endHorizontal, int startVertical, int endVertical) {
        ValueAnimator paddingLeftAnimator = ValueAnimator.ofInt(startHorzontal, endHorizontal);
        ValueAnimator paddingTopAnimator = ValueAnimator.ofInt(startVertical, endVertical);
        ValueAnimator paddingRightAnimator = ValueAnimator.ofInt(startHorzontal, endHorizontal);
        ValueAnimator paddingBottomAnimator = ValueAnimator.ofInt(startVertical, endVertical);

        paddingLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int leftPadding = (Integer) animation.getAnimatedValue();
                view.setPadding(leftPadding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }
        });

        paddingTopAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int topPadding = (Integer) animation.getAnimatedValue();
                view.setPadding(view.getPaddingLeft(), topPadding, view.getPaddingRight(), view.getPaddingBottom());
            }
        });

        paddingRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int rightPadding = (Integer) animation.getAnimatedValue();
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), rightPadding, view.getPaddingBottom());
            }
        });

        paddingBottomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int bottomPadding = (Integer) animation.getAnimatedValue();
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), bottomPadding);
            }
        });

        paddingLeftAnimator.setInterpolator(new DecelerateInterpolator());
        paddingTopAnimator.setInterpolator(new DecelerateInterpolator());
        paddingRightAnimator.setInterpolator(new DecelerateInterpolator());
        paddingBottomAnimator.setInterpolator(new DecelerateInterpolator());

        paddingLeftAnimator.setDuration(500); // Duration in milliseconds
        paddingTopAnimator.setDuration(500); // Duration in milliseconds
        paddingRightAnimator.setDuration(500); // Duration in milliseconds
        paddingBottomAnimator.setDuration(500); // Duration in milliseconds

        paddingLeftAnimator.start();
        paddingTopAnimator.start();
        paddingRightAnimator.start();
        paddingBottomAnimator.start();
    }
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return taskDates.size();
    }

    public static class TaskDateViewHolder extends RecyclerView.ViewHolder {
        public TextView dayText,dayOfWeek;
        public CardView cardView;
        public View view;
        public ImageView indicator;
        public LinearLayout linearLayout;


        public TaskDateViewHolder(View itemView) {
            super(itemView);
            indicator = itemView.findViewById(R.id.indicator);
            dayText = itemView.findViewById(R.id.dayText);
            dayOfWeek = itemView.findViewById(R.id.dayOfWeekText);
            cardView = itemView.findViewById(R.id.cardView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            view = itemView;
        }
    }
}
