package com.hashedin.marchantapp.Services.models.TransactionHistory;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.MutableLiveData;

import com.hashedin.marchantapp.Services.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Transactions extends BaseObservable {
    private String status;
    private List<Transaction> breedsList = new ArrayList<>();
    private MutableLiveData<List<Transaction>> breeds = new MutableLiveData<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addBreed(Transaction bd) {
        breedsList.add(bd);
    }

    public MutableLiveData<List<Transaction>> getBreeds() {
        return breeds;
    }

    public void fetchList() {
        Callback<Transactions> callback = new Callback<Transactions>() {
            @Override
            public void onResponse(Call<Transactions> call, Response<Transactions> response) {
                Transactions body = response.body();
                status = body.status;
                breeds.setValue(body.breedsList);
            }

            @Override
            public void onFailure(Call<Transactions> call, Throwable t) {
                Log.e("Test", t.getMessage(), t);
            }
        };

        List<Transaction> transactions = new ArrayList<>();

        for(int i=0;i<5;i++){
            Transaction transaction = new Transaction();
            transaction.setBreed("A"+i);
            transactions.add(transaction);
        }

        breeds.setValue(transactions);






        //Api.getApi().getBreeds().enqueue(callback);
    }
}