package com.example.taskmanagerfeatures;

import static android.app.PendingIntent.getActivity;
import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FocusDurationDialog extends BottomSheetDialogFragment {
    Activity activity;
    LinearLayout goto_previous_bottom_fragment;
    TextView previous_bottom_fragment_header, previous_bottom_fragment_select, time_denomination;
    NumberPicker time_picker;
    SharedPreferences preferences;
    int newVal2, fdDefaultValue, bdDefaultValue, lbeDefaultValue, prdDefaultValue, sdDefaultValue;
    String header, denominator;
    BottomSheetFragment bottomSheetFragment;

    public FocusDurationDialog(Activity activity, String header, String denominator) {
        this.activity = activity;
        this.header = header;
        this.denominator = denominator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_focus_duration_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goto_previous_bottom_fragment = view.findViewById(R.id.goto_previous_bottom_fragment);
        previous_bottom_fragment_header = view.findViewById(R.id.previous_bottom_fragment_header);
        previous_bottom_fragment_select = view.findViewById(R.id.previous_bottom_fragment_select);
        time_denomination = view.findViewById(R.id.time_denomination);
        time_picker = view.findViewById(R.id.time_picker);

        preferences = activity.getSharedPreferences("timeInMinutes", MODE_PRIVATE);
        fdDefaultValue = preferences.getInt("fdTimeNum", 10);
        bdDefaultValue = preferences.getInt("bdTimeNum", 10);
        lbeDefaultValue = preferences.getInt("lbeTimeNum", 1);
        prdDefaultValue = preferences.getInt("prdTimeNum", 10);
        sdDefaultValue = preferences.getInt("sdTimeNum", 15);

        previous_bottom_fragment_header.setText(header);
        time_denomination.setText(denominator);

        if (header.equals("Focus Duration")){
            time_picker.setMaxValue(60);
            time_picker.setMinValue(1);
            time_picker.setValue(fdDefaultValue);
        }else if (header.equals("Break Duration")){
            time_picker.setMaxValue(60);
            time_picker.setMinValue(1);
            time_picker.setValue(bdDefaultValue);
        }else if (header.equals("Long Break Every")){
            time_picker.setMaxValue(10);
            time_picker.setMinValue(1);
            time_picker.setValue(lbeDefaultValue);
        }else if (header.equals("Postpone Reminder Duration")){
            time_picker.setMaxValue(60);
            time_picker.setMinValue(1);
            time_picker.setValue(prdDefaultValue);
        }else if (header.equals("Sound Duration")){
            time_picker.setMaxValue(30);
            time_picker.setMinValue(10);
            time_picker.setValue(sdDefaultValue);
        }

        time_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                newVal2 = newVal;
            }
        });

        goto_previous_bottom_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                openFragment();
            }
        });

        previous_bottom_fragment_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                if (header.equals("Focus Duration")){
                    editor.putInt("fdTimeNum", newVal2);
                }else if (header.equals("Break Duration")){
                    editor.putInt("bdTimeNum", newVal2);
                }else if (header.equals("Long Break Every")){
                    editor.putInt("lbeTimeNum", newVal2);
                }else if (header.equals("Postpone Reminder Duration")){
                    editor.putInt("prdTimeNum", newVal2);
                }else if (header.equals("Sound Duration")){
                    editor.putInt("sdTimeNum", newVal2);
                }

                editor.commit();
                dismiss();
                openFragment();
            }
        });
    }

    public void openFragment(){
        bottomSheetFragment = new BottomSheetFragment(activity);
        bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
    }
}