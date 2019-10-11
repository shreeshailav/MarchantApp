package com.hashedin.marchantapp.viewmodel;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.Repository.ApiEndpoints;
import com.hashedin.marchantapp.Services.Repository.ApiService;
import com.hashedin.marchantapp.Services.models.TransactionHistory.Result;
import com.hashedin.marchantapp.Services.models.TransactionHistory.TransactionHistoryMain;
import com.hashedin.marchantapp.viewactivity.adapters.TransactionAdapter;
import com.hashedin.marchantapp.viewactivity.ui.history.HistoryFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionViewModel extends ViewModel {


    private ObservableArrayMap<String, String> images;
    public ObservableInt loading;
    public ObservableInt showEmpty;
    private MutableLiveData<List<Result>> results = new MutableLiveData<>();
    private TransactionHistoryMain transactionHistoryMain;
    private TransactionAdapter transactionAdapter;
    private MutableLiveData<Result> selected2;
    private ApiEndpoints endpoints;

    public TransactionViewModel() {
        endpoints = ApiService.getService();
    }

    public void init() {
        images = new ObservableArrayMap<>();
        loading = new ObservableInt(View.GONE);
        showEmpty = new ObservableInt(View.GONE);
        transactionHistoryMain = new TransactionHistoryMain();
        selected2 = new MutableLiveData<>();
        transactionAdapter = new TransactionAdapter(R.layout.transaction_list, this);


    }


    public MutableLiveData<List<Result>> getBreeds() {
        return results;
    }

    public void addItems(List<Result> results) {
        this.transactionAdapter.addItems(results);
    }


    public void setResultsInAdapter(List<Result> results) {
        this.transactionAdapter.setResults(results);
        this.transactionAdapter.notifyDataSetChanged();
    }

    public MutableLiveData<Result> getSelected() {
        return selected2;
    }

    public void onItemClick(Integer index) {
        Result db = getResulsAt(index);
        selected2.setValue(db);
    }

    public Result getResulsAt(Integer index) {
        if (results.getValue() != null &&
                index != null &&
                results.getValue().size() > index) {
            return results.getValue().get(index);
        }
        return null;
    }


    public TransactionAdapter getAdapter2() {
        return transactionAdapter;
    }

    public void fetchList2(String token) {
        getTransactionHistory(token);

    }

    public void getTransactionHistory(String token) {
        final MutableLiveData<List<Result>> resultMutableLiveData = new MutableLiveData<>();
        Call<TransactionHistoryMain> call = endpoints.getTransactionHistory(token);
        call.enqueue(new Callback<TransactionHistoryMain>() {
            @Override
            public void onResponse(Call<TransactionHistoryMain> call, Response<TransactionHistoryMain> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    //apiResponse.postValue(new ApiResponse(response.body()));
                    if (response.body().next != null) {
                        HistoryFragment.prevpagenumber = HistoryFragment.nextpagenumber;
                        HistoryFragment.nextpagenumber = response.body().next;
                    } else {
                        HistoryFragment.prevpagenumber = HistoryFragment.nextpagenumber;
                        HistoryFragment.nextpagenumber = "";
                    }
                    results.setValue(response.body().results);
                    loading.set(View.GONE);
                    showEmpty.set(View.GONE);

                } else {
                    Log.i("error code", "" + response.code());
                    loading.set(View.GONE);
                    showEmpty.set(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TransactionHistoryMain> call, Throwable t) {
                Log.i("error code", "error");
            }
        });

    }


}