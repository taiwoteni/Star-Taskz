package com.theteam.taskz.presentation.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.theteam.taskz.R;
import com.theteam.taskz.domain.entities.Workspace;


public class MembersTabFragment extends Fragment {

    private Workspace workspace;
    private LinearLayout empty_layout, loading_layout;
    private RecyclerView members_recycler_view;


    public MembersTabFragment(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.members_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        empty_layout = view.findViewById(R.id.empty_layout);
        loading_layout = view.findViewById(R.id.loading_layout);
        members_recycler_view = view.findViewById(R.id.members_recycler_view);

        empty_layout.setVisibility(View.VISIBLE);
        loading_layout.setVisibility(View.GONE);
        members_recycler_view.setVisibility(View.GONE);
    }
}