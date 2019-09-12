package com.hashedin.marchantapp.ViewActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private Button loginbtn;
   // private LoginViewModel loginViewModel;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
       // ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.a);

        setContentView(R.layout.activity_login);

        loginbtn = findViewById(R.id.btnLogin);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),QRCodeScannerActivity.class);
                startActivity(intent);
            }
        });


    }
}
