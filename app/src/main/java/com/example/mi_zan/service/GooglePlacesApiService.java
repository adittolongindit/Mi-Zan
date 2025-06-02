package com.example.mi_zan.service; // atau package service Anda

import com.example.mi_zan.model.SearchNearbyRequestBody;
import com.example.mi_zan.model.SearchNearbyResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GooglePlacesApiService {
    @POST("v1/places:searchNearby")
    Call<SearchNearbyResponseBody> searchNearbyPlaces(
            @Header("X-Goog-Api-Key") String apiKey,
            @Header("X-Goog-FieldMask") String fieldMask,
            @Body SearchNearbyRequestBody body
    );
}