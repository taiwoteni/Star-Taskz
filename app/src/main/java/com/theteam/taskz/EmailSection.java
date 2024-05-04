package com.theteam.taskz;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theteam.taskz.R;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.UnderlineTextView;

public class EmailSection extends Fragment {



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.email_section, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LoadableButton button = view.findViewById(R.id.loadable_button);
        final UnderlineTextView textView = view.findViewById(R.id.back);

        textView.setOnClickListener(view1 -> {
            ViewPagerDataHolder.viewPager.setCurrentItem(0, true);
        });
    }
}
