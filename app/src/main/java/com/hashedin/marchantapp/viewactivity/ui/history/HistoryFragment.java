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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.models.Transaction;
import com.hashedin.marchantapp.databinding.FragmentHistoryBinding;
import com.hashedin.marchantapp.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;




public class HistoryFragment extends Fragment {


    public static String fragmentname = null ;


    private HistoryViewModel historyViewModel;
    private TransactionViewModel transactionViewModel;
    FragmentHistoryBinding fragmentHistoryBinding;

    boolean isLoading = false;
    boolean isfirst = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        fragmentname = "History";


//         for (int i = 0; i < getFragmentManager().getBackStackEntryCount(); i++) {
//
//             FragmentManager.BackStackEntry result = getFragmentManager().getBackStackEntryAt(i);
//             String tag = result.getName();
//             Fragment fragment = null ;
//             if(tag !=null)
//                  fragment = getFragmentManager().findFragmentByTag(tag);
//
//
//             if(getFragmentManager().getBackStackEntryAt(i) instanceof QRCodeGenerateFragment){
//                 getFragmentManager().popBackStack(getFragmentManager().getBackStackEntryAt(i).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//             }
//
//         }


        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);

        fragmentHistoryBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_history, container, false);

        View root = fragmentHistoryBinding.getRoot();


        fragmentHistoryBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (getFragmentManager() != null)
//                    getFragmentManager().popBackStack();

                getActivity().onBackPressed();

            }
        });

        //fragmentHistoryBinding = DataBindingUtil.setContentView(getActivity(), R.layout.fragment_history);
        transactionViewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        if (savedInstanceState == null) {
            transactionViewModel.init();
        }
        fragmentHistoryBinding.setModel(transactionViewModel);


        setupListUpdate();


        // final TextView textView = root.findViewById(R.id.text_dashboard);
        historyViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        fragmentHistoryBinding.listOfTransaction.addOnScrollListener(recyclerViewOnScrollListener);
        fragmentHistoryBinding.avi.hide();


        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

       // getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }


    private void setupListClick() {

        transactionViewModel.getSelected().observe(this, new Observer<Transaction>() {
            @Override
            public void onChanged(Transaction transaction) {
                if (transaction != null) {

                    // Toast.makeText(getContext(),transaction.getBreed(),Toast.LENGTH_LONG).show();
                    FragmentManager fragmentManager = getFragmentManager();
                    TransactionDetailsFragment redeemFragment = new TransactionDetailsFragment();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, redeemFragment).addToBackStack(null).commit();

                }
            }
        });


    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);


            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();


            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

            //if (!isLoading && !isLastPage) {
            if (!isLoading && !isfirst) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {

                    loadMoreItems(totalItemCount - 1);
                }
            }
            isfirst = false;
            // }

        }
    };


    private void loadMoreItems(final int visibleItemCount) {

        isLoading = true;

        fragmentHistoryBinding.avi.show();


        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Transaction> transactions = new ArrayList<>();

                        for (int i = 0; i < 5; i++) {
                            Transaction transaction = new Transaction();
                            transaction.setBreed("A" + i);
                            transactions.add(transaction);
                        }
                        transactionViewModel.addItems(transactions);


                        if (fragmentHistoryBinding.listOfTransaction.getLayoutManager().getItemCount() > (visibleItemCount + 3)) {
                            //fragmentHistoryBinding.listOfTransaction.smoothScrollToPosition(visibleItemCount+1);
                            fragmentHistoryBinding.listOfTransaction.getLayoutManager().scrollToPosition(visibleItemCount + 3);


                        } else {
                            //fragmentHistoryBinding.listOfTransaction.smoothScrollToPosition(visibleItemCount);

                            fragmentHistoryBinding.listOfTransaction.getLayoutManager().scrollToPosition(visibleItemCount);

                        }
                        fragmentHistoryBinding.avi.hide();


                        isLoading = false;

                    }
                });


            }
        }, 2000);


    }




}