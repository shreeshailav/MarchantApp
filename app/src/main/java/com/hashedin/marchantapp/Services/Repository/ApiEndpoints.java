package com.hashedin.marchantapp.Services.Repository;

import com.hashedin.marchantapp.Services.models.Coupons;
import com.hashedin.marchantapp.Services.models.ReddemCoupon;
import com.hashedin.marchantapp.Services.models.UserCredentials;
import com.hashedin.marchantapp.Services.models.UserKey;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiEndpoints {
    String HTTPS_API_MARCHENT_URL = "http://city-rewards-dev.ap-southeast-1.elasticbeanstalk.com/";


    @GET("api/v1/coupons/{user}/")
    Call<Coupons> getCouponDetails(@Path("user") String user, @Header("Authorization") String token);


    // For Login
    @POST("merchant/login/")
    Call<UserKey> getLoginkey(@Body UserCredentials body);


    @POST("api/v1/coupons/{cid}/redeem/")
    Call<ReddemCoupon> getredeem(@Path("cid") String cid, @Header("Authorization") String token);



}