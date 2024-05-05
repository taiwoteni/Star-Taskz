package com.theteam.taskz;

public class AuthenticationDataHolder {
    static String firstName,lastName,email,password, dob;

    static void clear(){
        firstName = null;
        lastName = null;
        email = null;
        password = null;
        dob = null;
    }
}
