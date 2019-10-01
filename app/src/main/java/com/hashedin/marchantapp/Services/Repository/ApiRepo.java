package com.hashedin.marchantapp.Services.Repository;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hashedin.marchantapp.Services.models.Coupons;
import com.hashedin.marchantapp.Services.models.ReddemCoupon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepo{

    public ApiEndpoints endpoints;

    public ApiRepo() {
        endpoints = ApiService.getService();
    }

   
    public LiveData<ApiResponse> getPosts(String couponceode,String token) {

        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<Coupons> call = endpoints.getCouponDetails(couponceode,token);

        call.enqueue(new Callback<Coupons>() {
            @Override
            public void onResponse(Call<Coupons> call, Response<Coupons> response) {
                if (response.isSuccessful() && response.code() == 200 ) {
                    apiResponse.postValue(new ApiResponse(response.body()));
                }else {
                    Log.i("error code",""+response.code());
                    apiResponse.postValue(new ApiResponse(response.message()));

                }
            }

            @Override
            public void onFailure(Call<Coupons> call, Throwable t) {
                apiResponse.postValue(new ApiResponse(t));
            }
        });
        return apiResponse;
    }

    public LiveData<ApiResponse> getRedeemCoupon(String couponceode,String token) {

        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<ReddemCoupon> call = endpoints.getredeem(couponceode,token);
        call.enqueue(new Callback<ReddemCoupon>() {
            @Override
            public void onResponse(Call<ReddemCoupon> call, Response<ReddemCoupon> response) {


                if (response.isSuccessful() && response.code() == 201 ) {
                    apiResponse.postValue(new ApiResponse(response.body()));
                }else {
                    Log.i("error code",""+response.code());
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        apiResponse.postValue(new ApiResponse(jObjError.getString("detail")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ReddemCoupon> call, Throwable t) {
                apiResponse.postValue(new ApiResponse(t));
            }
        });




        return apiResponse;
    }




}