package com.example.taskmanagerfeatures;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SoundsBottomFragment extends BottomSheetDialogFragment {
    Activity activity;
    LinearLayout sound_goto_previous_bottom_fragment;
    TextView sound_bottom_fragment_header, sound_bottom_fragment_select;
    NumberPicker sound_picker;
    BottomSheetFragment bottomSheetFragment;
    SharedPreferences sharedPreferences;
    String header, soundName, defaultSoundF, defaultSoundB;
    int newVal2;

    public SoundsBottomFragment(Activity activity, String header) {
        this.activity = activity;
        this.header = header;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_sounds_bottom_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sound_goto_previous_bottom_fragment = view.findViewById(R.id.sound_goto_previous_bottom_fragment);
        sound_bottom_fragment_header = view.findViewById(R.id.sound_bottom_fragment_header);
        sound_bottom_fragment_select = view.findViewById(R.id.sound_bottom_fragment_select);
        sound_picker = view.findViewById(R.id.sound_picker);
        SoundsPickerModel.initSounds();
        sharedPreferences = activity.getSharedPreferences("sound_name", MODE_PRIVATE);

        sound_bottom_fragment_header.setText(header);
        sound_picker.setMaxValue(SoundsPickerModel.getSoundsPickerModelArrayList().size() - 1);
        sound_picker.setMinValue(0);
        sound_picker.setDisplayedValues(SoundsPickerModel.soundNames());

        sound_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                newVal2 = newVal;
            }
        });

        sound_bottom_fragment_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundName = SoundsPickerModel.getSoundsPickerModelArrayList().get(newVal2).getName();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (header.equals("Focus-ending Sound")){
                    editor.putString("focusSound", soundName);
                }else if (header.equals("Break-ending Sound")){
                    editor.putString("breakSound", soundName);
                }

                editor.commit();
                dismiss();
                openFragment();
            }
        });

        sound_goto_previous_bottom_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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