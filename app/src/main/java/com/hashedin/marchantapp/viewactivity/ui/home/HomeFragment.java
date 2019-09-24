package com.hashedin.marchantapp.viewactivity.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    FragmentHomeBinding fragmentHomeBinding ;
    NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        fragmentHomeBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false);

        View root = fragmentHomeBinding.getRoot();


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

         //root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
              //  textView.setText(s);
            }
        });

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);



        return root;
    }
    public void loadQRCodeScannerActivity(View view){
        if(navController!=null)
            navController.navigate(R.id.navigation_scan);
    }public void loadQRCodeGenerateFragment(View view){
        if(navController!=null)
            navController.navigate(R.id.navigation_qr);
    }public void loadHistoryFragment(View view){
        if(navController!=null)
            navController.navigate(R.id.navigation_history);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


}