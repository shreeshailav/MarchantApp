package com.hashedin.marchantapp.viewmodel;

import android.view.View;

import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hashedin.marchantapp.R;
import com.hashedin.marchantapp.Services.models.Transaction;
import com.hashedin.marchantapp.Services.models.TransactionHistory.Transactions;
import com.hashedin.marchantapp.viewactivity.adapters.TransactionAdapter;

import java.util.List;

public class TransactionViewModel extends ViewModel {

    private Transactions transaction;    // model
    private TransactionAdapter adapter;
    public MutableLiveData<Transaction> selected;
    public ObservableArrayMap<String, String> images;
    public ObservableInt loading;
    public ObservableInt showEmpty;




    public void init() {
        transaction = new Transactions();
        selected = new MutableLiveData<>();
        adapter = new TransactionAdapter(R.layout.transaction_list, this);
        images = new ObservableArrayMap<>();
        loading = new ObservableInt(View.GONE);
        showEmpty = new ObservableInt(View.GONE);



    }

    public void fetchList() {
        transaction.fetchList();

    }

    public MutableLiveData<List<Transaction>> getBreeds() {
        return transaction.getBreeds();
    }

    public void addItems(List<Transaction> transactions) {
        this.adapter.addItems(transactions);
    }

    public TransactionAdapter getAdapter() {
        return adapter;
    }

    public void setDogBreedsInAdapter(List<Transaction> breeds) {
        this.adapter.setDogBreeds(breeds);
        this.adapter.notifyDataSetChanged();
    }

    public MutableLiveData<Transaction> getSelected() {
        return selected;
    }

    public void onItemClick(Integer index) {
        Transaction db = getDogBreedAt(index);
        selected.setValue(db);
    }

    public Transaction getDogBreedAt(Integer index) {
        if (transaction.getBreeds().getValue() != null &&
                index != null &&
                transaction.getBreeds().getValue().size() > index) {
            return transaction.getBreeds().getValue().get(index);
        }
        return null;
    }






//    public void fetchDogBreedImagesAt(Integer index) {
//        DogBreed dogBreed = getDogBreedAt(index);
//        if (dogBreed != null && !images.containsKey(dogBreed.getBreed())) {
//            dogBreed.fetchImages(new DogImagesCallback(dogBreed.getBreed()) {
//                @Override
//                public void onResponse(Call<DogBreedImages> call, Response<DogBreedImages> response) {
//                    DogBreedImages body = response.body();
//                    if (body.getImages() != null && body.getImages().length > 0) {
//                        String thumbnailUrl = body.getImages()[0];
//                        images.put(getBreed(), thumbnailUrl);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<DogBreedImages> call, Throwable t) {
//                    Log.e("Test", t.getMessage(), t);
//                }
//            });
//        }
//    }


}