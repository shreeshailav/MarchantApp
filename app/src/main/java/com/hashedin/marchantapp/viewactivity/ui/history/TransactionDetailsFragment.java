package com.hashedin.marchantapp.viewactivity.ui.history;

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
import com.hashedin.marchantapp.databinding.FragmentTransactionDetailsBinding;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;

public class TransactionDetailsFragment extends Fragment {

    FragmentTransactionDetailsBinding fragmentTransactionDetailsBinding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        MerchantMainActivity.currentFragment = "TransactionDetailsFragment";

        fragmentTransactionDetailsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_transaction_details, container, false);

        View root = fragmentTransactionDetailsBinding.getRoot();

        fragmentTransactionDetailsBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null)
                    getFragmentManager().popBackStack();
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