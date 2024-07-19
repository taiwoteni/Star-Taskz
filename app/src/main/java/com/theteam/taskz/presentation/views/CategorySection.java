package com.theteam.taskz.presentation.views;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.theteam.taskz.R;
import com.theteam.taskz.data.models.AuthenticationDataHolder;
import com.theteam.taskz.presentation.adapters.ViewPagerAdapter;
import com.theteam.taskz.presentation.viewmodels.LoginViewModel;
import com.theteam.taskz.utils.enums.AccountType;

public class CategorySection extends Fragment {

    private SelectableButton businessAccount,familyAccount,personalAccount;
    private LoadableButton button;
    private UnderlineTextView back;
    private LoginViewModel loginViewModel;

    @Override
    public void onResume() {
        super.onResume();

        sortOutAccountType();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        businessAccount = (SelectableButton) view.findViewById(R.id.business_account);
        familyAccount = (SelectableButton) view.findViewById(R.id.family_account);
        personalAccount = (SelectableButton) view.findViewById(R.id.personal_account);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        button = view.findViewById(R.id.loadable_button);
        back = view.findViewById(R.id.back);

        if(AuthenticationDataHolder.selecAccountType == null){
            AuthenticationDataHolder.selecAccountType = AccountType.Personal;
            sortOutAccountType();
        }

        back.setOnClickListener(view1 -> {
            loginViewModel.back();
        });

        businessAccount.setOnClickListener(view1 -> {
            businessAccount.select(true);
            personalAccount.select(false);
            familyAccount.select(false);

            AuthenticationDataHolder.selecAccountType = AccountType.Business;
        });
        familyAccount.setOnClickListener(view1 -> {
            familyAccount.select(true);
            personalAccount.select(false);
            businessAccount.select(false);

            AuthenticationDataHolder.selecAccountType = AccountType.Family;
        });
        personalAccount.setOnClickListener(view1 -> {
            personalAccount.select(true);
            businessAccount.select(false);
            familyAccount.select(false);

            AuthenticationDataHolder.selecAccountType = AccountType.Personal;
        });

        button.setOnClickListener(view1 -> {
            final AccountType accountType = AuthenticationDataHolder.selecAccountType;
            button.startLoading();

            final boolean isNotPersonal = accountType!= AccountType.Personal;
            final boolean isBusiness = accountType == AccountType.Business;
            ViewPagerAdapter adapter = (ViewPagerAdapter)loginViewModel.getViewPager().getValue().getAdapter();
            Log.v("VIEW_PAGER_ADAPTER", "Switched to " + accountType.name());
            if(isBusiness){
                adapter.addJobSection();
            }else{
                adapter.useNormalSection();
            }

            Handler handler = new Handler();
            handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            button.stopLoading();
                            loginViewModel.next();
                        }
                    },
                    2500
            );
        });
    }

    private void sortOutAccountType(){
        if(AuthenticationDataHolder.selecAccountType == AccountType.Business){
            businessAccount.select(true);
            personalAccount.select(false);
            familyAccount.select(false);
        }
        else if(AuthenticationDataHolder.selecAccountType == AccountType.Family){
            familyAccount.select(true);
            personalAccount.select(false);
            businessAccount.select(false);
        }
        else{
            personalAccount.select(true);
            businessAccount.select(false);
            familyAccount.select(false);
        }
    }
}
