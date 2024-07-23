package com.theteam.taskz.presentation.views;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.presentation.adapters.TaskDateListAdapter;
import com.theteam.taskz.data.models.TaskDateModel;
import com.theteam.taskz.presentation.adapters.ViewPagerAdapter;
import com.theteam.taskz.presentation.transformers.CustomLinearLayoutManager;
import com.theteam.taskz.presentation.viewmodels.TaskDatesViewModel;
import com.theteam.taskz.presentation.viewmodels.TasksViewModel;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class TasksFragment extends Fragment {

    public static float MILLISECONDS_PER_INCH = 100f;
    private RecyclerView recyclerView;
    private TaskDateListAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private CircleImageView profile_image;

    private UserModel user;

    private ViewPager2 viewPager2;

    private TasksViewModel tasksViewModel;
    private TaskDatesViewModel taskDatesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tasks_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.dates_recycler_view);
        viewPager2 = view.findViewById(R.id.view_pager);
        profile_image = view.findViewById(R.id.profile_image);

        user = new UserModel(requireActivity());

        if(user.hasProfile()){
            Glide.with(this)
                    .load(user.profile())
                    .placeholder(R.drawable.avatar)
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .error(R.drawable.avatar)
                    .addListener(new RequestListener<Drawable>() {

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            profile_image.setImageResource(R.drawable.avatar);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            profile_image.setImageDrawable(drawable);
                            profile_image.setScaleX(1f);
                            profile_image.setScaleY(1f);
                            return true;
                        }
                    })
                    .into(profile_image);
        }

        //Configure viewPager2
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setUserInputEnabled(false);

        // Instantiate and configure LinearLayoutManager for recyclerView
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        linearLayoutManager.setSmoothScrollbarEnabled(false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        
        // We call the ViewModelProviders to enable access to this Fragment activities
        // .. children (Date Items in RecyclerView) and TasksList
        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        taskDatesViewModel = new ViewModelProvider(requireActivity()).get(TaskDatesViewModel.class);



        initialize();


    }

    private void initialize() {
        Calendar calendar = Calendar.getInstance();

        ArrayList<TaskDateModel> taskDateModels = new ArrayList<>();

        ArrayList<Fragment> pages = new ArrayList<>();


        ArrayList<Integer> monthsWith30Days = new ArrayList<>();
        monthsWith30Days.add(4);
        monthsWith30Days.add(6);
        monthsWith30Days.add(9);
        monthsWith30Days.add(11);

        int max = monthsWith30Days.contains(calendar.get(Calendar.MONTH))? 30 : 31;
        for(int i = 1; i <= max; i++) {
            final Calendar current = (Calendar) calendar.clone();
            current.set(Calendar.DAY_OF_MONTH, i);
            taskDateModels.add(new TaskDateModel(current));
        }
        for(final TaskDateModel dateModel: taskDateModels){
            pages.add(new TasksPageFragment(dateModel));
        }

        adapter = new TaskDateListAdapter(taskDateModels, requireActivity());
        recyclerView.setAdapter(adapter);
        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(requireActivity(), pages);
        viewPager2.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();

        Log.i("TASKS_FRAGMENT", "View Pager size " + viewPager2.getAdapter().getItemCount());
        Log.i("TASKS_FRAGMENT", "Recycler View size " + recyclerView.getAdapter().getItemCount());


        // We want to scroll to the current day if the it is the current month
        if(calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)){
            final Calendar selectedCalendar = Calendar.getInstance();
            recyclerView.scrollToPosition(selectedCalendar.get(Calendar.DAY_OF_MONTH)-1);
            viewPager2.setCurrentItem(selectedCalendar.get(Calendar.DAY_OF_MONTH)-1);
        }

        taskDatesViewModel.getSelectedDate().observe(getViewLifecycleOwner(), _calendar -> {
            if(_calendar.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)){
                viewPager2.setCurrentItem(_calendar.get(Calendar.DAY_OF_MONTH)-1, true);
            }
        });





    }
}
