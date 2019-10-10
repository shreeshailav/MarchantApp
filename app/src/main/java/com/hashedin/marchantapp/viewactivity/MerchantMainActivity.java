package com.hashedin.marchantapp.viewactivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.viewactivity.ui.history.HistoryFragment;

public class MerchantMainActivity extends AppCompatActivity {

    public static String currentFragment = "";

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onBackPressed() {


        boolean handled = false;

        BottomNavigationView navView = findViewById(R.id.nav_view);

        int selectedID = navView.getSelectedItemId();



        if (selectedID == R.id.navigation_home && !currentFragment.equalsIgnoreCase("HomeFragment")) {
            navView.setSelectedItemId(R.id.navigation_home);
            handled = true;

        }


        if (HistoryFragment.fragmentname != null && HistoryFragment.fragmentname.equalsIgnoreCase("History")) {

            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


            handled = true;
            HistoryFragment.fragmentname = null;
            super.onBackPressed();

        }

        if (!handled) {
            super.onBackPressed();
        }
    }

}
//,R.id.navigation_qr