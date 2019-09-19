package com.hashedin.marchantapp.viewmodel;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.hashedin.marchantapp.Services.Models.Coupons;
import com.hashedin.marchantapp.Services.Repository.ApiRepo;
import com.hashedin.marchantapp.Services.Repository.ApiResponse;


public class CouponDetailViewModel extends ViewModel {


    private MediatorLiveData<ApiResponse> mApiResponse;
    private ApiRepo mApiRepo;


    public ObservableField<Coupons> couponsObservableField = new ObservableField<>();


    public CouponDetailViewModel() {
        mApiResponse = new MediatorLiveData<>();
        mApiRepo = new ApiRepo();
    }


    public LiveData<ApiResponse> getData(String couponcode,String token) {

        if(!TextUtils.isEmpty(couponcode)){
            mApiResponse.addSource(mApiRepo.getPosts(couponcode,token), new Observer<ApiResponse>() {
                @Override
                public void onChanged(@Nullable ApiResponse apiResponse)            {
                    mApiResponse.setValue(apiResponse);
                }
            });
        }

        return mApiResponse;
    }

    public LiveData<ApiResponse> getRedeem(String couponcode,String token) {

        if(!TextUtils.isEmpty(couponcode)){
            mApiResponse.addSource(mApiRepo.getRedeemCoupon(couponcode,token), new Observer<ApiResponse>() {
                @Override
                public void onChanged(@Nullable ApiResponse apiResponse)            {
                    mApiResponse.setValue(apiResponse);
                }
            });
        }

        return mApiResponse;
    }

}
