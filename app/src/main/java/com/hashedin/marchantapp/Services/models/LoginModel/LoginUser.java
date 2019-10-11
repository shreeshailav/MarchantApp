package com.hashedin.marchantapp.Services.models.LoginModel;


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


}
