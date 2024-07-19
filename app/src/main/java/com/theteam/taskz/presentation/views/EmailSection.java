package com.theteam.taskz.presentation.views;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.AuthenticationDataHolder;
import com.theteam.taskz.domain.repositories.AuthenticationRepository;
import com.theteam.taskz.presentation.viewmodels.LoginViewModel;
import com.theteam.taskz.utils.others.JsonUtils;

import java.util.regex.Pattern;

public class EmailSection extends Fragment {

    private LoadableButton button;

    private Dialog dialog;
    private TextInputFormField emailForm,passwordForm,confirmPasswordForm;
    private UnderlineTextView textView;

    private AuthenticationRepository authenticationRepository;

    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.email_section, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        authenticationRepository = new AuthenticationRepository(requireActivity().getApplicationContext());

        button = view.findViewById(R.id.loadable_button);
        textView = view.findViewById(R.id.back);
        emailForm = view.findViewById(R.id.email_form);
        passwordForm = view.findViewById(R.id.password_form);
        confirmPasswordForm = view.findViewById(R.id.confirm_password_form);
        if(AuthenticationDataHolder.email != null){
            emailForm.setText(AuthenticationDataHolder.email);
        }

        textView.setOnClickListener(view1 -> {
            loginViewModel.back();
        });

        button.setOnClickListener(view1 -> {
            button.startLoading();
            disableAllInput();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                button.stopLoading();
                if(!validateEmail()){
                    showErrorMessage("Email is wrongly formatted");
                    enableAllInput();
                    return;
                }
                if(!validatePassword()){
                    showErrorMessage("Password should be at least 8 digits");
                    enableAllInput();
                    return;
                }
                if(!passwordForm.getText().trim().equals(confirmPasswordForm.getText().trim())){
                    showErrorMessage("Passwords are not matching");
                    enableAllInput();
                    return;
                }
                enableAllInput();
                AuthenticationDataHolder.email = emailForm.getText().trim();
                AuthenticationDataHolder.password = passwordForm.getText().trim();

                createAccount();
            }, 2500);
        });
    }


    void disableAllInput(){
        emailForm.setEnabled(false);
        passwordForm.setEnabled(false);
        confirmPasswordForm.setEnabled(false);
    }
    void enableAllInput(){
        emailForm.setEnabled(true);
        passwordForm.setEnabled(true);
        confirmPasswordForm.setEnabled(true);
    }

    boolean validateEmail(){
        final String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(regex, emailForm.getText());
    }
    boolean validatePassword(){

        return passwordForm.getText().length()>= 8;
    }
    void showErrorMessage(final String message){
        Toast.makeText(requireActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    void createAccount(){

        showStarLoading("Validating Credentials");
        authenticationRepository.registerUser(
                null,
                jsonObject -> {
                    Log.i("API_RESPONSE", JsonUtils.prettyPrint(jsonObject.toString()));
                    dialog.dismiss();
                    loginViewModel.next();
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
