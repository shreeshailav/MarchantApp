package com.hashedin.marchantapp.viewactivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiEndpoints;
import com.hashedin.marchantapp.Services.models.LoginUser;
import com.hashedin.marchantapp.Services.models.UserCredentials;
import com.hashedin.marchantapp.Services.models.UserKey;
import com.hashedin.marchantapp.databinding.ActivityLoginBinding;
import com.hashedin.marchantapp.viewactivity.Utility.DialogsUtils;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.LoginViewModel;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hashedin.marchantapp.Services.Repository.ApiEndpoints.HTTPS_API_MARCHENT_URL;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    boolean show_password = true ;
    PopupWindow optionspu;


    public static final String MyPREFERENCES = "LoginDetails" ;
    public static String key = null;
    PrefManager prefManager;
    ApiEndpoints marchentServices;
    ProgressDialog myDialog;


    Locale myLocale;
    String currentLanguage = "en", currentLang;

    private ActivityLoginBinding activityLoginBinding;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();



        //String mess = getResources().getString(R.string.hide_pwd);


        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        currentLanguage = getIntent().getStringExtra(currentLang);


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


        activityLoginBinding.loginButton.setAlpha(.5f);
        activityLoginBinding.loginButton.setEnabled(false);


        activityLoginBinding.loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {



            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(TextUtils.isEmpty(activityLoginBinding.loginPassword.getText().toString()) || TextUtils.isEmpty(activityLoginBinding.loginUsername.getText().toString())){
                        activityLoginBinding.loginButton.setAlpha(.5f);
                        activityLoginBinding.loginButton.setEnabled(false);
                    }else if(!TextUtils.isEmpty(activityLoginBinding.loginPassword.getText().toString()) && !TextUtils.isEmpty(activityLoginBinding.loginUsername.getText().toString())){

                        activityLoginBinding.loginButton.setAlpha(1f);
                        activityLoginBinding.loginButton.setEnabled(true);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }); activityLoginBinding.loginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(TextUtils.isEmpty(activityLoginBinding.loginPassword.getText().toString()) || TextUtils.isEmpty(activityLoginBinding.loginUsername.getText().toString())){
                        activityLoginBinding.loginButton.setAlpha(.5f);
                        activityLoginBinding.loginButton.setEnabled(false);
                    }else if(!TextUtils.isEmpty(activityLoginBinding.loginPassword.getText().toString()) && !TextUtils.isEmpty(activityLoginBinding.loginUsername.getText().toString())){

                        activityLoginBinding.loginButton.setAlpha(1f);
                        activityLoginBinding.loginButton.setEnabled(true);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        activityLoginBinding.loginPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (activityLoginBinding.loginPassword.getRight() - activityLoginBinding.loginPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here


                        if(show_password){
                            activityLoginBinding.loginPassword.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                            activityLoginBinding.loginPassword.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_action_visiible),null);

                            show_password = false ;
                        }else {
                            activityLoginBinding.loginPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            activityLoginBinding.loginPassword.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password
                            activityLoginBinding.loginPassword.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_action_invisiible),null);
                            show_password = true ;

                        }


                        return true;
                    }
                }

                return false;
            }

        });





//                (new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//
//                if(event.getAction() == MotionEvent.ACTION_UP) {
//                    if(event.getRawX() >= (editComment.getRight() - editComment.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                        // your action here
//
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });





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

                        Intent intent = new Intent(getBaseContext(),MerchantMainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                 }else {

                    activityLoginBinding.invalidtext.setVisibility(View.VISIBLE);
                    activityLoginBinding.invalidtext.setText("You have entered an invalid employee id or password.");

                    //showOptions(getBaseContext(),"Invalid Credentials, Try again");
                    //Toast.makeText(getBaseContext(),"Failed",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<UserKey> call, Throwable t) {
                Log.i("ERROR","Error : "+t.getMessage());
                if (myDialog!=null)
                    myDialog.dismiss();
               // showOptions(getBaseContext(),"Failed. Try again");

                activityLoginBinding.invalidtext.setVisibility(View.VISIBLE);
                activityLoginBinding.invalidtext.setText("Unable to reach server.");


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

    private void showOptions(Context mcon, String msg){
        try{


            LayoutInflater inflater = (LayoutInflater) mcon.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.rq_error_popup,null);


            TextView textView = layout.findViewById(R.id.errormessage);

            textView.setText(msg);

            Button closebtn = layout.findViewById(R.id.closebtn);

            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionspu.dismiss();
                }
            });

            optionspu = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            optionspu.setAnimationStyle(R.style.popup_window_animation);
            optionspu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            optionspu.setFocusable(true);
            optionspu.setOutsideTouchable(true);
            optionspu.update(0, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            optionspu.showAtLocation(layout, Gravity.CENTER, 0, 0);

            optionspu.setOutsideTouchable(false);


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }









}
