package com.hashedin.marchantapp.Services.Repository;

import com.hashedin.marchantapp.Services.Models.Coupons;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MarchentServices {

    //String HTTPS_API_MARCHENT_URL = "https://api.github.com/";
    String HTTPS_API_MARCHENT_URL = "http://192.168.43.91:8001/";

    @Headers({"Content-Type: application/json"})
    @GET("api/v1/coupons/{user}")
    Call<List<Coupons>> getProjectList(
            @Header("X-CSRFToken") String token,
            @Path("user") String user
    );



//    @Headers({"Content-Type: application/json"})
//    @POST("api/v1/coupons/{user}")
//    Call<List<Coupons>> getProjectList(
//            @Header("X-CSRFToken") String token,
//            @Path("user") String user,
//            @Body RequestBody params
//    );

}
