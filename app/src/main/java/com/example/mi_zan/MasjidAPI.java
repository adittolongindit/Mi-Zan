package com.example.mi_zan;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MasjidAPI {
    @GET("api/get-masjid")
    Call<MasjidResponse> getMasjidNearby(
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("radius") String radius
    );
}