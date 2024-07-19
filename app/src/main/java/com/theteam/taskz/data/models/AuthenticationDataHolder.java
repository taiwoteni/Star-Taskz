package com.theteam.taskz.data.models;

import com.theteam.taskz.utils.enums.AccountType;

public class AuthenticationDataHolder {
    public static String firstName,lastName,email,password, dob,jobDescription,jobTitle;
    public static AccountType selecAccountType;

    public static void clear(){
        firstName = null;
        lastName = null;
        selecAccountType = null;
        jobTitle = null;
        jobDescription = null;
        email = null;
        password = null;
        dob = null;
    }
}
