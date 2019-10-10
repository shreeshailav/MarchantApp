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
import com.hashedin.marchantapp.viewactivity.adapters.TransactionAdapter2;
import com.hashedin.marchantapp.viewactivity.ui.history.HistoryFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionViewModel2 extends ViewModel {


    public ObservableArrayMap<String, String> images;
    public ObservableInt loading;
    public ObservableInt showEmpty;



    private MutableLiveData<List<Result>> results = new MutableLiveData<>();


    private TransactionHistoryMain transactionHistoryMain;
    private TransactionAdapter2 transactionAdapter2;
    public MutableLiveData<Result> selected2;


    public ApiEndpoints endpoints;

    public TransactionViewModel2() {
        endpoints = ApiService.getService();
    }

    public void init() {

        images = new ObservableArrayMap<>();
        loading = new ObservableInt(View.GONE);
        showEmpty = new ObservableInt(View.GONE);

        transactionHistoryMain = new TransactionHistoryMain();
        selected2 = new MutableLiveData<>();
        transactionAdapter2 = new TransactionAdapter2(R.layout.transaction_list, this);



    }



    public MutableLiveData<List<Result>> getBreeds() {
        return results;
    }

    public void addItems(List<Result> results) {
        this.transactionAdapter2.addItems(results);
    }

//    public TransactionAdapter2 getAdapter() {
//        return transactionAdapter2;
//    }

    public void setDogBreedsInAdapter(List<Result> results) {
        this.transactionAdapter2.setDogBreeds(results);
        this.transactionAdapter2.notifyDataSetChanged();
    }

    public MutableLiveData<Result> getSelected() {
        return selected2;
    }

    public void onItemClick(Integer index) {
        Result db = getDogBreedAt(index);
        selected2.setValue(db);
    }

    public Result getDogBreedAt(Integer index) {
        if (results.getValue() != null &&
                index != null &&
                results.getValue().size() > index) {
            return results.getValue().get(index);
        }
        return null;
    }


    ///////////////////////////////////////////////////

    public void addItems2(List<Result> results) {
        this.transactionAdapter2.addItems(results);
    }
    public TransactionAdapter2 getAdapter2() {
        return transactionAdapter2;
    }

    public void fetchList2(String token) {
       // transaction.fetchList();

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



                    if(response.body().next!=null) {
                        HistoryFragment.prevpagenumber = HistoryFragment.nextpagenumber ;
                        HistoryFragment.nextpagenumber = response.body().next;
                    }
                    else {
                        HistoryFragment.prevpagenumber = HistoryFragment.nextpagenumber ;
                        HistoryFragment.nextpagenumber = "";
                    }

                    results.setValue(response.body().results);

                    loading.set(View.GONE);

                     showEmpty.set(View.GONE);

                } else {
                    Log.i("error code", "" + response.code());

                   loading.set(View.GONE);

                   showEmpty.set(View.VISIBLE);



                    // showEmpty = new ObservableInt(View.VISIBLE);

//                    List<Result> transactions = new ArrayList<>();
//
//                    for(int i=0;i<5;i++){
//                        Result transaction = new Result();
//                        transaction.amount = ""+10+i;
//                        transactions.add(transaction);
//                    }
//
//                    results.setValue(transactions);




                    // JSONObject jObjError = new JSONObject(response.errorBody().string());
                    // apiResponse.postValue(new ApiResponse(jObjError.getString("detail")));

                }
            }

            @Override
            public void onFailure(Call<TransactionHistoryMain> call, Throwable t) {
               // apiResponse.postValue(new ApiResponse(t));
                 Log.i("error code", "error");
            }
        });

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


//    public void fetchList() {
//        Callback<TransactionHistoryMain> callback = new Callback<TransactionHistoryMain>() {
//            @Override
//            public void onResponse(Call<TransactionHistoryMain> call, Response<TransactionHistoryMain> response) {
//                Transactions body = response.body();
//                status = body.status;
//                breeds.setValue(body.breedsList);
//            }
//
//            @Override
//            public void onFailure(Call<TransactionHistoryMain> call, Throwable t) {
//                Log.e("Test", t.getMessage(), t);
//            }
//        };
//
//        List<Transaction> transactions = new ArrayList<>();
//
//        for(int i=0;i<5;i++){
//            Transaction transaction = new Transaction();
//            transaction.setBreed("A"+i);
//            transactions.add(transaction);
//        }
//
//        breeds.setValue(transactions);
//
//
//
//
//
//
//        //Api.getApi().getBreeds().enqueue(callback);
//    }


}