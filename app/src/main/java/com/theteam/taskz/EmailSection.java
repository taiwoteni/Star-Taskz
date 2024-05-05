package com.theteam.taskz;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theteam.taskz.R;
import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;
import com.theteam.taskz.view_models.UnderlineTextView;

import java.util.Objects;
import java.util.regex.Pattern;

public class EmailSection extends Fragment {

    private LoadableButton button;
    private TextInputFormField emailForm,passwordForm,confirmPasswordForm;
    private UnderlineTextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.email_section, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = view.findViewById(R.id.loadable_button);
        textView = view.findViewById(R.id.back);
        emailForm = view.findViewById(R.id.email_form);
        passwordForm = view.findViewById(R.id.password_form);
        confirmPasswordForm = view.findViewById(R.id.confirm_password_form);

        if(AuthenticationDataHolder.email != null){
            emailForm.setText(AuthenticationDataHolder.email);
        }

        textView.setOnClickListener(view1 -> {
            ViewPagerDataHolder.viewPager.setCurrentItem(0, true);
        });

        button.setOnClickListener(view1 -> {
            button.startLoading();
            disableAllInput();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                button.stopLoading();
                if(!validateEmail()){
                    showErrorMessage("Email Wrongly Formatted !!");
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
}
