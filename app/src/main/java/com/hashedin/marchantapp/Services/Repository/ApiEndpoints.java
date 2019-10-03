package com.hashedin.marchantapp.Services.Repository;

import com.hashedin.marchantapp.Services.models.Coupons;
import com.hashedin.marchantapp.Services.models.GenerateQR;
import com.hashedin.marchantapp.Services.models.QRCodeGenerateModel.QRGenModel;
import com.hashedin.marchantapp.Services.models.QRInfo;
import com.hashedin.marchantapp.Services.models.ReddemCoupon;
import com.hashedin.marchantapp.Services.models.TransacrionRequest.TransactionReq;
import com.hashedin.marchantapp.Services.models.UserCredentials;
import com.hashedin.marchantapp.Services.models.UserKey;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiEndpoints {
    //String HTTPS_API_MARCHENT_URL = "http://city-rewards-dev.ap-southeast-1.elasticbeanstalk.com/";
    String HTTPS_API_MARCHENT_URL = "http://a59f280a.ngrok.io";


    @GET("merchant/api/v1/coupons/{user}/")
    Call<Coupons> getCouponDetails(@Path("user") String user, @Header("Authorization") String token);


    // For Login
    @POST("merchant/api/v1/auth/login/")
    Call<UserKey> getLoginkey(@Body UserCredentials body);


    @POST("merchant/api/v1/coupons/{cid}/redeem/")
    Call<ReddemCoupon> getredeem(@Path("cid") String cid, @Header("Authorization") String token);


    @POST("merchant/api/v1/staff/transactions/register/")
    Call<GenerateQR> getRequestQR(@Body QRInfo body, @Header("Authorization") String token);


    @GET("merchant/api/v1/staff/transactions/{uuid}/")
    Observable<QRGenModel> getTransactionDetail(@Path("uuid") String user, @Header("Authorization") String token);



    @POST("merchant/api/v1/staff/transactions/{uuid}/accept/")
    Call<TransactionReq> getTransactionApproveReq(@Path("uuid") String user, @Header("Authorization") String token);


    @POST("merchant/api/v1/staff/transactions/{uuid}/decline/")
    Call<QRGenModel> getTransactionDeclineReq(@Path("uuid") String user, @Header("Authorization") String token);




}