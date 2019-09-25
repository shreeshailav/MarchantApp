package com.hashedin.marchantapp.viewactivity.ui.history;

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

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.models.Transaction;
import com.hashedin.marchantapp.databinding.FragmentHistoryBinding;
import com.hashedin.marchantapp.databinding.FragmentHomeBinding;
import com.hashedin.marchantapp.viewmodel.TransactionViewModel;

import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private TransactionViewModel transactionViewModel;
    FragmentHistoryBinding fragmentHistoryBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);



//        fragmentHistoryBinding = DataBindingUtil.setContentView(getActivity(), R.layout.fragment_history);
//        transactionViewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
//        if (savedInstanceState == null) {
//            transactionViewModel.init();
//        }
//        fragmentHistoryBinding.setModel(transactionViewModel);
//        setupListUpdate();




       // final TextView textView = root.findViewById(R.id.text_dashboard);
        historyViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
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


    private void setupListUpdate() {
        transactionViewModel.loading.set(View.VISIBLE);
        transactionViewModel.fetchList();
        transactionViewModel.getBreeds().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                transactionViewModel.loading.set(View.GONE);
                if (transactions.size() == 0) {
                    transactionViewModel.showEmpty.set(View.VISIBLE);
                } else {
                    transactionViewModel.showEmpty.set(View.GONE);
                    transactionViewModel.setDogBreedsInAdapter(transactions);
                }
            }
        });

        setupListClick();
    }

    private void setupListClick() {

        transactionViewModel.getSelected().observe(this, new Observer<Transaction>() {
            @Override
            public void onChanged(Transaction transaction) {
                if(transaction!=null){

                }
            }
        });


    }
}