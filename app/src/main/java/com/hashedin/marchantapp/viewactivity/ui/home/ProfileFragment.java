package com.hashedin.marchantapp.viewactivity.ui.home;

import android.content.Intent;
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

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.databinding.FragmentProfileBinding;
import com.hashedin.marchantapp.viewactivity.LoginActivity;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding fragmentProfileBinding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        fragmentProfileBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_profile, container, false);

        View root = fragmentProfileBinding.getRoot();



        fragmentProfileBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
            }
        });



        fragmentProfileBinding.signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefManager prefManager = new PrefManager(getContext());
                prefManager.logout();

                if(prefManager.isUserLogedOut()) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    if(getActivity()!=null)
                        getActivity().finish();
                }

            }
        });


        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


}