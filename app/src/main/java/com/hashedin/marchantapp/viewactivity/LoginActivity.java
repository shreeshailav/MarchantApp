package com.hashedin.marchantapp.viewactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Models.Credentials;
import com.hashedin.marchantapp.Services.Models.ReddemCoupon;
import com.hashedin.marchantapp.Services.Models.UserCredentials;
import com.hashedin.marchantapp.Services.Models.UserKey;
import com.hashedin.marchantapp.Services.Repository.MarchentServices;
import com.hashedin.marchantapp.databinding.ActivityLoginBinding;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hashedin.marchantapp.Services.Repository.MarchentServices.HTTPS_API_MARCHENT_URL;

public class LoginActivity extends AppCompatActivity {

    // private LoginViewModel loginViewModel;


    public static final String MyPREFERENCES = "LoginDetails" ;
    public static String key = null;
    PrefManager prefManager;
    MarchentServices marchentServices;
    ProgressDialog myDialog;

    private ActivityLoginBinding activityLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();


        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);


        requestPermission();


        activityLoginBinding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                }
                else if(activityLoginBinding.loginUsername.length()<=0){
                    activityLoginBinding.loginUsername.setError("Enter valid username");
                }else if(activityLoginBinding.loginPassword.length()<=0){
                    activityLoginBinding.loginPassword.setError("Enter valid password");
                }else {
                    getlogin(activityLoginBinding.loginUsername.getText().toString(),activityLoginBinding.loginPassword.getText().toString());
                }
            }
        });

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





    }

    public void getlogin(String username,String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTPS_API_MARCHENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        marchentServices = retrofit.create(MarchentServices.class);

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
