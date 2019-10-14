package com.hashedin.marchantapp.viewactivity.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.FragmentHomeBinding;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.ui.profile.ProfileFragment;
import com.hashedin.marchantapp.viewactivity.ui.qrcodegenerate.QRCodeGenerateFragment;

public class HomeFragment extends Fragment {

    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        MerchantMainActivity.currentFragment = "HomeFragment" ;
        FragmentHomeBinding fragmentHomeBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);

        View root = fragmentHomeBinding.getRoot();

        fragmentHomeBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                ProfileFragment redeemFragment = new ProfileFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();
            }
        });

        fragmentHomeBinding.scanqrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_scan);
            }
        });


        fragmentHomeBinding.generateqrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final FragmentManager fragmentManager = getFragmentManager();
                QRCodeGenerateFragment redeemFragment = new QRCodeGenerateFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();
            }
        });

        fragmentHomeBinding.historybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_history);
            }
        });
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }



}