package com.hashedin.marchantapp.Services.Repository;

import com.hashedin.marchantapp.Services.Models.ClientCredential;
import com.hashedin.marchantapp.Services.Models.Coupons;
import com.hashedin.marchantapp.Services.Models.Credentials;
import com.hashedin.marchantapp.Services.Models.UserCredentials;
import com.hashedin.marchantapp.Services.Models.UserKey;
import com.hashedin.marchantapp.Services.Models.ReddemCoupon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface MarchentServices {

    //String HTTPS_API_MARCHENT_URL = "http://12d06fce.ngrok.io";
    String HTTPS_API_MARCHENT_URL = "http://city-rewards-dev.ap-southeast-1.elasticbeanstalk.com/";
    String HTTPS_API_CLIENT_URL = "http://city-rewards-dev.ap-southeast-1.elasticbeanstalk.com/";



//    @GET("api/v1/coupons/465d5955-de23-4220-a96f-8c3ffaa00ae5/")
//    Call<List<Coupons>> getProjectList();





    // For Login
    @POST("merchant/login/")
    Call<UserKey> getLoginkey(@Body UserCredentials body);

    @GET("api/v1/coupons/{user}/")
    Call<Coupons> getProjectList(@Path("user") String user,@Header("Authorization") String token);


//    @POST("api/v1/coupons/{cid}/redeem/")
//    Call<ReddemCoupon> getredeem(@Path("cid") String cid, @Body Credentials body,@Header("Authorization") String token);

    @POST("api/v1/coupons/{cid}/redeem/")
    Call<ReddemCoupon> getredeem(@Path("cid") String cid,@Header("Authorization") String token);





//
//    @GET("api/v1/handshake/")
//    Call<ClientCredential> getCredential(@Header("handshake-token") String token);
//
//    @GET
//    Call<List<Coupons>> getProjectList3(@Url String url);

//    @GET
//    Call<Users> getUsers(@Url String url);

//    @Header("X-CSRFToken") String token,
//    @Path("user") String user

//    @Headers({"Content-Type: application/json"})
//    @POST("api/v1/coupons/{user}")
//    Call<List<Coupons>> getProjectList(
//            @Header("X-CSRFToken") String token,
//            @Path("user") String user,
//            @Body RequestBody params
//    );

}
