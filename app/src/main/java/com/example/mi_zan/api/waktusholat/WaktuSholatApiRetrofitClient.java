package com.example.mi_zan.api.waktusholat;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaktuSholatApiRetrofitClient {
    // URL dasar API tetap sama karena kita hanya mengganti nama di kode
    private static final String PRAYER_TIME_API_BASE_URL = "https://api.banghasan.com/";
    private static Retrofit retrofitInstance = null; // Diberi nama variabel yang jelas

    public static WaktuSholatApiService getApiService() {
        if (retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(PRAYER_TIME_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance.create(WaktuSholatApiService.class);
    }
}