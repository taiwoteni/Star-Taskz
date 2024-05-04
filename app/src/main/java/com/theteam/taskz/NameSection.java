package com.theteam.taskz;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theteam.taskz.R;
import com.theteam.taskz.ViewPagerDataHolder;
import com.theteam.taskz.view_models.LoadableButton;

import java.util.Objects;

public class NameSection extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.name_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LoadableButton button = view.findViewById(R.id.loadable_button);
        final TextView alreadyHaveAccount = view.findViewById(R.id.have_account);

        alreadyHaveAccount.setOnClickListener(view1 -> {
            Objects.requireNonNull(getActivity()).finish();
        });

        button.setOnClickListener(view1 -> {
            button.startLoading();
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            button.stopLoading();
                            ViewPagerDataHolder.viewPager.setCurrentItem(1, true);
                        }
                    },
                    2500
            );
        });

    }
}
