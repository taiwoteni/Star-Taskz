package com.theteam.taskz;

public class AuthenticationDataHolder {
    public static String firstName,lastName,email,password, dob;

    public static void clear(){
        firstName = null;
        lastName = null;
        email = null;
        password = null;
        dob = null;
    }
}
