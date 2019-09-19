package com.hashedin.marchantapp.Services.Models;

import android.util.Patterns;


public class LoginUser {

    private String userName;
    private String userPassword;

    public LoginUser(String userName, String userPassword) {
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public String getuserName() {
        return userName;
    }

    public String getuserPassword() {
        return userPassword;
    }

//    public boolean isEmailValid() {
//        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches();
//    }


    public boolean isPasswordLengthGreaterThan5() {
        return getuserPassword().length() > 2;
    }

}
