package com.theteam.taskz.data.models;

import com.theteam.taskz.utils.enums.AccountType;

public class AuthenticationDataHolder {
    public static String firstName,lastName,email,password, dob,jobDescription,jobTitle, urlPhoto;
    public static AccountType selecAccountType;
    public static boolean googleSignIn = false;


    public static void clear(){
        firstName = null;
        lastName = null;
        selecAccountType = null;
        jobTitle = null;
        jobDescription = null;
        googleSignIn = false;
        email = null;
        password = null;
        urlPhoto = null;
        dob = null;
    }
}
