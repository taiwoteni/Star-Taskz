package com.theteam.taskz;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.theteam.taskz.view_models.LoadableButton;
import com.theteam.taskz.view_models.TextInputFormField;

import java.util.Objects;

public class NameSection extends Fragment {
    private LoadableButton button;
    private TextView alreadyHaveAccount;
    private TextInputFormField firstNameForm, lastNameForm, dateOfBirthForm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.name_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = view.findViewById(R.id.loadable_button);
        alreadyHaveAccount = view.findViewById(R.id.have_account);
        firstNameForm = view.findViewById(R.id.first_name_form);
        lastNameForm = view.findViewById(R.id.last_name_form);
        dateOfBirthForm = view.findViewById(R.id.dob_form);




        alreadyHaveAccount.setOnClickListener(view1 -> {
            Objects.requireNonNull(getActivity()).finish();
        });

        button.setOnClickListener(view1 -> {
            button.startLoading();
            disableAllInput();
            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            button.stopLoading();
                            if(!validateName(firstNameForm.getText())){
                                showErrorMessage("First Name is Empty");
                                enableAllInput();
                                return;
                            }
                            if(!validateName(lastNameForm.getText())){
                                showErrorMessage("Last Name is Empty");
                                enableAllInput();
                                return;
                            }
                            enableAllInput();
                            AuthenticationDataHolder.firstName = firstNameForm.getText().trim();
                            AuthenticationDataHolder.lastName = lastNameForm.getText().trim();

                            ViewPagerDataHolder.viewPager.setCurrentItem(1, true);
                        }
                    },
                    2500
            );
        });

    }

    void disableAllInput(){
        firstNameForm.setEnabled(false);
        lastNameForm.setEnabled(false);
        dateOfBirthForm.setEnabled(false);
    }
    void enableAllInput(){
        firstNameForm.setEnabled(true);
        lastNameForm.setEnabled(true);
        dateOfBirthForm.setEnabled(true);
    }
    boolean validateName(final String name){
        return !name.trim().isEmpty();
    }
    void showErrorMessage(final String message){
        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
