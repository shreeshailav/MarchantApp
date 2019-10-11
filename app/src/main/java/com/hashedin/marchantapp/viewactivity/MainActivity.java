package com.hashedin.marchantapp.viewactivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "LoginDetails";
    public static String key = null;
    PrefManager prefManager;
    Spinner spinner;
    Locale myLocale;
    String currentLanguage = "en", currentLang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //setLocale("hi");
        prefManager = new PrefManager(this);
        //prefManager.logout();
        key = prefManager.getKey();
        if (!prefManager.isUserLogedOut()) {
            // Intent intent = new Intent(this, QRCodeScannerActivity.class);
            Intent intent = new Intent(this, MerchantMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public void setLocale(String localeName) {
         myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.putExtra(currentLang, localeName);
        startActivity(refresh);
    }
}
