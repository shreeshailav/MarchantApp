package com.hashedin.marchantapp.viewactivity;

 import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;


public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "LoginDetails" ;
    public static String key = null;
    PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);

        //prefManager.logout();

        if(!prefManager.isUserLogedOut()){
            Intent intent = new Intent(this, QRCodeScannerActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
