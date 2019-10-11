package com.hashedin.marchantapp.Services.Repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hashedin.marchantapp.Services.models.QRCodeScanModel.Coupons;
import com.hashedin.marchantapp.Services.models.QRCodeGenerateModel.GenerateQR;
import com.hashedin.marchantapp.Services.models.QRCodeGenerateModel.QRInfo;
import com.hashedin.marchantapp.Services.models.RedeemCoupon.RedeemCoupon;
import com.hashedin.marchantapp.Services.models.TransacrionRequest.TransactionReq;
import com.hashedin.marchantapp.Services.models.TransactionHistory.TransactionHistoryMain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepo {

    public ApiEndpoints endpoints;

    public ApiRepo() {

        endpoints = ApiService.getService();

    }


    public LiveData<ApiResponse> getPosts(String couponceode, String token) {

        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<Coupons> call = endpoints.getCouponDetails(couponceode, token);

        call.enqueue(new Callback<Coupons>() {
            @Override
            public void onResponse(Call<Coupons> call, Response<Coupons> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    apiResponse.postValue(new ApiResponse(response.body()));
                } else {
                    Log.i("error code", "" + response.code());
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

    public LiveData<ApiResponse> getRedeemCoupon(String couponceode, String token) {

        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<RedeemCoupon> call = endpoints.getredeem(couponceode, token);
        call.enqueue(new Callback<RedeemCoupon>() {
            @Override
            public void onResponse(Call<RedeemCoupon> call, Response<RedeemCoupon> response) {


                if (response.isSuccessful() && response.code() == 201) {
                    apiResponse.postValue(new ApiResponse(response.body()));
                } else {
                    Log.i("error code", "" + response.code());
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        apiResponse.postValue(new ApiResponse(jObjError.getString("detail")));

                    } catch (JSONException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    } catch (IOException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<RedeemCoupon> call, Throwable t) {
                apiResponse.postValue(new ApiResponse(t));
            }
        });


        return apiResponse;
    }


    public LiveData<ApiResponse> getRQUUID(QRInfo qrInfo, String token) {

        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();

        Call<GenerateQR> call = endpoints.getRequestQR(qrInfo, token);

        call.enqueue(new Callback<GenerateQR>() {
            @Override
            public void onResponse(Call<GenerateQR> call, Response<GenerateQR> response) {


                if (response.isSuccessful() && response.code() == 201) {
                    apiResponse.postValue(new ApiResponse(response.body()));
                } else {
                    Log.i("error code", "" + response.code());
                    try {

                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        apiResponse.postValue(new ApiResponse(jObjError.getString("detail")));

                    } catch (JSONException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    } catch (IOException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<GenerateQR> call, Throwable t) {
                apiResponse.postValue(new ApiResponse(t));
            }
        });


        return apiResponse;
    }


    public LiveData<ApiResponse> getTransactionAcceptReq(String couponceode, String token) {

        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<TransactionReq> call = endpoints.getTransactionApproveReq(couponceode, token);
        call.enqueue(new Callback<TransactionReq>() {
            @Override
            public void onResponse(Call<TransactionReq> call, Response<TransactionReq> response) {


                if (response.isSuccessful() && response.code() == 200) {
                    apiResponse.postValue(new ApiResponse(response.body()));
                } else {
                    Log.i("error code", "" + response.code());
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        apiResponse.postValue(new ApiResponse(jObjError.getString("detail")));

                    } catch (JSONException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    } catch (IOException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<TransactionReq> call, Throwable t) {
                apiResponse.postValue(new ApiResponse(t));
            }
        });
        return apiResponse;
    }


    public LiveData<ApiResponse> getTransactionDeclineReq(String couponceode, String token) {

        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<TransactionReq> call = endpoints.getTransactionDeclineReq(couponceode, token);
        call.enqueue(new Callback<TransactionReq>() {
            @Override
            public void onResponse(Call<TransactionReq> call, Response<TransactionReq> response) {


                if (response.isSuccessful() && response.code() == 200) {
                    apiResponse.postValue(new ApiResponse(response.body()));
                } else {
                    try {
                        Log.i("error code", "" + response.code());

                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        apiResponse.postValue(new ApiResponse(jObjError.getString("detail")));

                    } catch (JSONException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    } catch (IOException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<TransactionReq> call, Throwable t) {
                apiResponse.postValue(new ApiResponse(t));
            }
        });
        return apiResponse;
    }

    public LiveData<ApiResponse> getTransactionHistory(String pagenumber,String token) {

        final MutableLiveData<ApiResponse> apiResponse = new MutableLiveData<>();
        Call<TransactionHistoryMain> call = endpoints.getTransactionHistoryPagination(pagenumber,token);
        call.enqueue(new Callback<TransactionHistoryMain>() {
            @Override
            public void onResponse(Call<TransactionHistoryMain> call, Response<TransactionHistoryMain> response) {


                if (response.isSuccessful() && response.code() == 200) {
                    apiResponse.postValue(new ApiResponse(response.body()));
                } else {
                    try {
                        Log.i("error code", "" + response.code());

                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        apiResponse.postValue(new ApiResponse(jObjError.getString("detail")));

                    } catch (JSONException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    } catch (IOException e) {
                        apiResponse.postValue(new ApiResponse(response.body()));

                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<TransactionHistoryMain> call, Throwable t) {
                apiResponse.postValue(new ApiResponse(t));
            }
        });
        return apiResponse;
    }


}