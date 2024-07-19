package com.theteam.taskz.presentation.views;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.AuthenticationDataHolder;
import com.theteam.taskz.presentation.viewmodels.LoginViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BirthdaySection extends Fragment {
    private LoadableButton loadable_button;
    private TextInputFormField dob_form;
    private UnderlineTextView back;
    private Calendar birthday = Calendar.getInstance();
    private LoginViewModel loginViewModel;


    public BirthdaySection() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.birthday_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadable_button = (LoadableButton) view.findViewById(R.id.loadable_button);
        dob_form = (TextInputFormField) view.findViewById(R.id.dob_form);
        back = (UnderlineTextView) view.findViewById(R.id.back);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        back.setOnClickListener(view1 -> {
            loginViewModel.back();
        });

        dob_form.setOnClickListener(view1 -> {
            showDatePicker();
        });

        loadable_button.setOnClickListener(view1 -> {
            loadable_button.startLoading();
            dob_form.setEnabled(false);
            new Handler().postDelayed(
                    () -> {
                        loadable_button.stopLoading();
                        loginViewModel.next();
                    },
                    2000
            );
        });

    }

    void showDatePicker(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int _year, int _month, int day) {
                calendar.set(Calendar.MONTH, _month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                String dayString = String.valueOf(day);
                String monthString = String.valueOf(_month+1);

                final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());
                final SimpleDateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                AuthenticationDataHolder.dob = secondFormat.format(calendar.getTime());
                dob_form.setText(dateFormat.format(calendar.getTime()));
                loadable_button.setText("NEXT");
            }
        },year,month,dayOfMonth);
        datePickerDialog.show();
    }

    void showErrorMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }



}