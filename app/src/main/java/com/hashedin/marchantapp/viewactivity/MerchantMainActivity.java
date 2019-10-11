package com.hashedin.marchantapp.viewactivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.viewactivity.Utility.ProfilePrefManager;

public class MerchantMainActivity extends AppCompatActivity {

    public static String currentFragment = ""; // To Track and manage Back stack

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_merchant_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_scan, R.id.navigation_support)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ProfilePrefManager profilePrefManager = new ProfilePrefManager(this);
        if (!profilePrefManager.isUserProfileEmpty()) {
            int themeID = R.style.FontSizeSmall;
            String textsize = profilePrefManager.getTextsize();
            if (textsize.equalsIgnoreCase("Small"))
                themeID = R.style.FontSizeSmall;
            else if (textsize.equalsIgnoreCase("Medium"))
                themeID = R.style.FontSizeMedium;
            else if (textsize.equalsIgnoreCase("Large"))
                themeID = R.style.FontSizeLarge;
            else if (textsize.equalsIgnoreCase("ExtraLarge"))
                themeID = R.style.FontSizeExtraLarge;
            setTheme(themeID);
        }


    }

    @Override
    public void onBackPressed() {
        boolean handled = false;
        BottomNavigationView navView = findViewById(R.id.nav_view);
        int selectedID = navView.getSelectedItemId();
        if (selectedID == R.id.navigation_home && !currentFragment.equalsIgnoreCase("HomeFragment")) {
            navView.setSelectedItemId(R.id.navigation_home);
            handled = true;

        } else if (selectedID == R.id.navigation_history && currentFragment.equalsIgnoreCase("History")) {
            navView.setSelectedItemId(R.id.navigation_home);
            handled = true;

        } else if (selectedID == R.id.navigation_history && !currentFragment.equalsIgnoreCase("History")) {
            navView.setSelectedItemId(R.id.navigation_history);
            handled = true;

        } else if (selectedID == R.id.navigation_scan && currentFragment.equalsIgnoreCase("QRCodeScannerFragment")) {
            navView.setSelectedItemId(R.id.navigation_home);
            handled = true;

        } else if (selectedID == R.id.navigation_scan && !currentFragment.equalsIgnoreCase("QRCodeScannerFragment")) {
            navView.setSelectedItemId(R.id.navigation_scan);
            handled = true;

        } else if (selectedID == R.id.navigation_support && currentFragment.equalsIgnoreCase("SupportFragment")) {
            navView.setSelectedItemId(R.id.navigation_home);
            handled = true;

        } else if (selectedID == R.id.navigation_support && !currentFragment.equalsIgnoreCase("SupportFragment")) {
            navView.setSelectedItemId(R.id.navigation_support);
            handled = true;

        }
        if (!handled) {
            super.onBackPressed();
        }
    }

}
