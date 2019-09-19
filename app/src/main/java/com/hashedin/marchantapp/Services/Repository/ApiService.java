package com.hashedin.marchantapp.Services.Repository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hashedin.marchantapp.Services.Repository.ApiEndpoints.HTTPS_API_MARCHENT_URL;


public final class ApiService {

    private static ApiEndpoints endpoints;

    public static ApiEndpoints getService() {
        if (endpoints == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(HTTPS_API_MARCHENT_URL)
                    .build();

            endpoints = retrofit.create(ApiEndpoints.class);
        }

        return endpoints;

    }

}