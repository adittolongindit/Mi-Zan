package com.example.mi_zan.api.wilayah;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WilayahIdRetrofitClient {
    private static final String WILAYAH_ID_BASE_URL = "https://wilayah.id/api/";
    private static Retrofit retrofitInstance = null;

    public static WilayahIdApiService getApiService() {
        if (retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(WILAYAH_ID_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance.create(WilayahIdApiService.class);
    }
}