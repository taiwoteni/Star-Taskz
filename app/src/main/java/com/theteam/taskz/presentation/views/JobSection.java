package com.theteam.taskz.presentation.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.AuthenticationDataHolder;
import com.theteam.taskz.data.models.UserModel;
import com.theteam.taskz.domain.repositories.AuthenticationRepository;
import com.theteam.taskz.presentation.viewmodels.LoginViewModel;
import com.theteam.taskz.utils.others.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class JobSection extends Fragment {
    private TextInputFormField job_description, job_title;
    private LoadableButton loadableButton;
    private UnderlineTextView back;

    private Dialog dialog;
    private AuthenticationRepository authenticationRepository;

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
        authenticationRepository = new AuthenticationRepository(requireActivity());

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

                if (AuthenticationDataHolder.googleSignIn){
                    createAccount();
                    return;
                }
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

    void createAccount(){

        showStarLoading("Validating Credentials");
        authenticationRepository.registerUser(
                null,
                jsonObject -> {
                    final JSONObject object = jsonObject;
                    Log.i("API_RESPONSE", JsonUtils.prettyPrint(jsonObject.toString()));
                    try {
                        object.put("accountType", AuthenticationDataHolder.selecAccountType.name());
                        object.put("jobTitle", AuthenticationDataHolder.jobTitle);
                        object.put("jobDescription", AuthenticationDataHolder.jobDescription);
                        object.put("password", AuthenticationDataHolder.password);
                        UserModel.saveUserData(JsonUtils.convertToHashMap(object), requireActivity());
                        dialog.dismiss();

                        Intent intent = new Intent(requireActivity().getApplicationContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                volleyError -> {
                    Log.e("API_RESPONSE", volleyError.toString());
                    showStarError("Couldn't validate credentials");

                }
        );

    }

    private void showStarError(String message){
        if(dialog!= null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        else{
            dialog = new Dialog(requireActivity());
        }


        View contentView = getLayoutInflater().inflate(R.layout.star_error_dialog, null);
        final LoadableButton loadableButton = contentView.findViewById(R.id.go_button);
        final TextView contentText = contentView.findViewById(R.id.content_text);

        contentText.setText(message);

        loadableButton.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(true);
        try{
            dialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private void showStarSuccess(String message, String label) {
        if(dialog!= null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        else{
            dialog = new Dialog(requireActivity());
        }


        View contentView = getLayoutInflater().inflate(R.layout.star_intro_dialog, null);
        final LoadableButton loadableButton = contentView.findViewById(R.id.go_button);
        final TextView contentText = contentView.findViewById(R.id.content_text);
        final TextView contentTitle = contentView.findViewById(R.id.content_title);
        final UnderlineTextView skipText = contentView.findViewById(R.id.skip_button);


        contentTitle.setText("Hey There!");
        contentText.setText(message);
        loadableButton.setText(label);
        skipText.setVisibility(View.GONE);

        loadableButton.setOnClickListener(view -> {
            Intent intent = requireActivity().getIntent();
            if(intent.hasExtra("logged in")){
                intent.removeExtra("logged in");
            }
            dialog.dismiss();
        });

        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(true);
        try{
            dialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private void showStarLoading(String message){
        if(dialog!= null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }
        else{
            dialog = new Dialog(requireActivity());
        }


        View contentView = getLayoutInflater().inflate(R.layout.star_loading_dialog, null);
        final TextView contentText = contentView.findViewById(R.id.content_text);
        contentText.setText(message);

        dialog.setContentView(contentView);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        dialog.setCancelable(false);
        try{
            dialog.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}