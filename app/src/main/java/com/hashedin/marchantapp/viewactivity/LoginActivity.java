package com.hashedin.marchantapp.viewactivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Models.LoginUser;
import com.hashedin.marchantapp.Services.Models.UserCredentials;
import com.hashedin.marchantapp.Services.Models.UserKey;
import com.hashedin.marchantapp.Services.Repository.ApiEndpoints;
import com.hashedin.marchantapp.databinding.ActivityLoginBinding;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.LoginViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hashedin.marchantapp.Services.Repository.ApiEndpoints.HTTPS_API_MARCHENT_URL;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;


    public static final String MyPREFERENCES = "LoginDetails" ;
    public static String key = null;
    PrefManager prefManager;
    ApiEndpoints marchentServices;
    ProgressDialog myDialog;

    private ActivityLoginBinding activityLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);


        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);


        requestPermission();


        activityLoginBinding.showHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    activityLoginBinding.loginPassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                }else {
                    activityLoginBinding.loginPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    activityLoginBinding.loginPassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password
                }
            }
        });


        activityLoginBinding.setLifecycleOwner(this);
        activityLoginBinding.setLoginviewmodel(loginViewModel);
        loginViewModel.getUser().observe(this, new Observer<LoginUser>() {
            @Override
            public void onChanged(LoginUser loginUser) {
                if (TextUtils.isEmpty(loginUser.getuserName())) {
                    activityLoginBinding.loginUsername.setError("Enter valid username");
                    activityLoginBinding.loginUsername.requestFocus();
                }else if (TextUtils.isEmpty(loginUser.getuserPassword())) {
                    activityLoginBinding.loginPassword.setError("Enter valid password");
                    activityLoginBinding.loginPassword.requestFocus();
                }else {
                    getlogin(loginUser.getuserName(),loginUser.getuserPassword());
                }
            }
        });
    }

    public void getlogin(String username,String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTPS_API_MARCHENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        marchentServices = retrofit.create(ApiEndpoints.class);

        UserCredentials userCredentials = new UserCredentials(username,password);

        Call<UserKey> couponsdata = marchentServices.getLoginkey(userCredentials);
        myDialog = DialogsUtils.showProgressDialog(LoginActivity.this, "Loading please wait");

        couponsdata.enqueue(new Callback<UserKey>() {
            @Override
            public void onResponse(Call<UserKey> call, Response<UserKey> response) {
                Log.i("Responce","response"+response.toString());

                if (myDialog!=null)
                    myDialog.dismiss();

                if(response.code() ==200) {
                    UserKey userKey = response.body();


                    if(userKey.key!=null && userKey.key.length()>0){
                        prefManager = new PrefManager(getBaseContext());
                        prefManager.saveLoginDetails(userKey.key);

                        Intent intent = new Intent(getBaseContext(),QRCodeScannerActivity.class);
                        startActivity(intent);
                        finish();
                    }

                 }else {
                    Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<UserKey> call, Throwable t) {
                Log.i("ERROR","Error : "+t.getMessage());
                if (myDialog!=null)
                    myDialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length < 1) {
            requestPermission();
        } else {

        }
    }
    void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }
}
