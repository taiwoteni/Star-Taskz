package com.theteam.taskz.presentation.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.AuthenticationDataHolder;
import com.theteam.taskz.presentation.viewmodels.LoginViewModel;


public class JobSection extends Fragment {
    private TextInputFormField job_description, job_title;
    private LoadableButton loadableButton;
    private UnderlineTextView back;

    private LoginViewModel loginViewModel;

    public JobSection() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.job_section, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(AuthenticationDataHolder.jobTitle != null){
            job_title.setText(AuthenticationDataHolder.jobTitle.trim());
        }
        if(AuthenticationDataHolder.jobDescription != null){
            job_description.setText(AuthenticationDataHolder.jobDescription.trim());
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        back = (UnderlineTextView) view.findViewById(R.id.back);
        loadableButton = (LoadableButton) view.findViewById(R.id.loadable_button);
        job_title = (TextInputFormField) view.findViewById(R.id.job_title_form);
        job_description = (TextInputFormField) view.findViewById(R.id.job_description_form);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        back.setOnClickListener(view1 -> {
            loginViewModel.back();
        });

        loadableButton.setOnClickListener(view1 -> {
            disableInputs();
            loadableButton.startLoading();
            new Handler().postDelayed(() -> {

                if(job_title.getText().trim().isEmpty() || job_description.getText().trim().isEmpty()){
                    showErrorMessage("Job Details are empty");
                    enableInputs();
                    loadableButton.stopLoading();
                    return;
                }

                AuthenticationDataHolder.jobDescription=job_description.getText().trim();
                AuthenticationDataHolder.jobTitle = job_title.getText().trim();
                loadableButton.stopLoading();
                loginViewModel.next();

            }, 2000);
        });




    }

    void showErrorMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void disableInputs(){
        job_description.setEnabled(false);
        job_title.setEnabled(false);
    }

    private void enableInputs(){
        job_title.setEnabled(true);
        job_description.setEnabled(true);
    }
}