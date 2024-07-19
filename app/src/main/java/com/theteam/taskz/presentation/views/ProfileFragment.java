package com.theteam.taskz.presentation.views;

import android.os.Bundle;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.presentation.adapters.ViewPagerAdapter;

public class ProfileFragment extends Fragment {
    private ImageView goToSettings;
    private TextView name_text,job_text;

    private UserModel user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_view_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = new UserModel(requireActivity());

        goToSettings = view.findViewById(R.id.settings_icon);
        name_text = (TextView) view.findViewById(R.id.name_text);
        job_text = (TextView) view.findViewById(R.id.job_title_text);

        name_text.setText(user.firstName() + (user.lastName().isEmpty()?"":(" "+ user.lastName())));
        job_text.setText(user.jobTitle());



        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), SettingsActivity.class));
            }
        });
    }
}