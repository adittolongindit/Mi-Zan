package com.example.mi_zan.network;

import com.example.mi_zan.model.JadwalResponse;
import com.example.mi_zan.model.LokasiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("sholat/kota/cari/{keyword}")
    Call<LokasiResponse> searchKota(@Path("keyword") String keyword);

    @GET("sholat/kota/semua")
    Call<LokasiResponse> getAllKota();

    @GET("sholat/jadwal/{idkota}/{tahun}/{bulan}")
    Call<JadwalResponse> getJadwalSholat(
            @Path("idkota") String idKota,
            @Path("tahun") String tahun,
            @Path("bulan") String bulan
    );
}