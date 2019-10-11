package com.hashedin.marchantapp.viewmodel;

import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hashedin.marchantapp.Services.models.LoginModel.LoginUser;


public class LoginViewModel extends ViewModel {


    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> userPassword = new MutableLiveData<>();

    private MutableLiveData<LoginUser> userMutableLiveData;

    public MutableLiveData<LoginUser> getUser() {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        return userMutableLiveData;

    }

    public void onClick(View view) {
        LoginUser loginUser = new LoginUser(userName.getValue(), userPassword.getValue());
        userMutableLiveData.setValue(loginUser);

    }

}
