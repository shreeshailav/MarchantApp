package com.hashedin.marchantapp.viewactivity.ui.history;

import android.app.ProgressDialog;
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
import com.hashedin.marchantapp.Services.Repository.ApiResponse;
import com.hashedin.marchantapp.Services.models.TransactionHistory.Result;
import com.hashedin.marchantapp.Services.models.TransactionHistory.TransactionHistoryMain;
import com.hashedin.marchantapp.databinding.FragmentHistoryBinding;
import com.hashedin.marchantapp.viewactivity.MerchantMainActivity;
import com.hashedin.marchantapp.viewactivity.Utility.PrefManager;
import com.hashedin.marchantapp.viewmodel.CouponDetailViewModel;
import com.hashedin.marchantapp.viewmodel.TransactionViewModel;

import java.util.List;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;


public class HistoryFragment extends Fragment {

    private TransactionViewModel transactionViewModel;
    private FragmentHistoryBinding fragmentHistoryBinding;

    private boolean isLoading = false;
    private boolean isfirst = true;

    private CouponDetailViewModel viewModel;
    ProgressDialog myDialog;
    private String auth_token;

    public static String nextpagenumber = "";
    public static String prevpagenumber = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MerchantMainActivity.currentFragment = "History";
        fragmentHistoryBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_history, container, false);
        View root = fragmentHistoryBinding.getRoot();
        PrefManager prefManager = new PrefManager(getContext());
        auth_token = "token " + prefManager.getKey();
        fragmentHistoryBinding.backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        transactionViewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        if (savedInstanceState == null) {
            transactionViewModel.init();
        }
        fragmentHistoryBinding.setModel(transactionViewModel);
        setupListUpdate();
        fragmentHistoryBinding.listOfTransaction.addOnScrollListener(recyclerViewOnScrollListener);
        fragmentHistoryBinding.avi.hide();
        viewModel = ViewModelProviders.of(this).get(CouponDetailViewModel.class);
        getMoreHistory();
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
        transactionViewModel.fetchList2(auth_token);
        transactionViewModel.getBreeds().observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> transactions) {
                transactionViewModel.loading.set(View.GONE);
                if (transactions.size() == 0) {
                    transactionViewModel.showEmpty.set(View.VISIBLE);
                } else {
                    transactionViewModel.showEmpty.set(View.GONE);
                    transactionViewModel.setResultsInAdapter(transactions);
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
    }


    private void setupListClick() {
        transactionViewModel.getSelected().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result != null) {
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
            if (!isLoading && !isfirst) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    if (nextpagenumber.contains("page=")) {
                        String pagenumber = nextpagenumber.split("page=")[1];
                        getHistory(pagenumber);
                    }
                }
            }
            isfirst = false;


        }
    };


    public void getHistory(String pagenumber) {
        isLoading = true;
        fragmentHistoryBinding.avi.show();
        viewModel.getTransactionHistory(pagenumber, auth_token);
    }

    public void getMoreHistory() {
        viewModel.getTransactionHistory("0", auth_token).observe(this, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse apiResponse) {
                isLoading = false;
                fragmentHistoryBinding.avi.hide();
                if (apiResponse == null) {
                    // handle error here
                    return;
                }
                if (apiResponse.transactionHistoryMain != null && apiResponse.getError() == null) {
                    TransactionHistoryMain transactionHistoryMain = apiResponse.transactionHistoryMain;
                    if (transactionHistoryMain.next != null) {
                        HistoryFragment.prevpagenumber = HistoryFragment.nextpagenumber;
                        HistoryFragment.nextpagenumber = transactionHistoryMain.next;
                    } else {
                        HistoryFragment.prevpagenumber = HistoryFragment.nextpagenumber;
                        HistoryFragment.nextpagenumber = "";
                    }
                    transactionViewModel.addItems(transactionHistoryMain.results);

                } else if (apiResponse.errorMessage != null) {
                    String failedstr = apiResponse.errorMessage; //getResources().getString(R.string.paid_Failed);
                    //showOptions(getContext(), failedstr);
                } else {
                    Throwable e = apiResponse.getError();

                }
            }
        });
    }

}