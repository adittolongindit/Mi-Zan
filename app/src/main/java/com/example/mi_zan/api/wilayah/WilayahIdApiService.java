package com.example.mi_zan.api.wilayah;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WilayahIdApiService {
    @GET("provinces.json")
    Call<WilayahIdProvincesResponse> getProvinces();

    @GET("regencies/{province_code}.json")
    Call<WilayahIdRegenciesResponse> getRegencies(@Path("province_code") String provinceCode);
}