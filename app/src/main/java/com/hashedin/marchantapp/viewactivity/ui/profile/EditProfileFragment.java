package com.hashedin.marchantapp.viewactivity.ui.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.hashedin.marchantapp.databinding.FragmentEditProfileBinding;
import com.hashedin.marchantapp.viewactivity.ui.qrcodescanner.RedeemFragment;

public class EditProfileFragment extends Fragment implements RedeemFragment.OnFragmentInteractionListener{


    FragmentEditProfileBinding fragmentEditProfileBinding;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fragmentEditProfileBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_edit_profile, container, false);

        View root = fragmentEditProfileBinding.getRoot();



        initialize();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void initialize(){


    }


    @Override
    public void onResume() {
        super.onResume();



    }

    @Override
    public void onPause() {
        super.onPause();
     }



    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("onFragmentInteraction", String.valueOf(uri));
    }
}