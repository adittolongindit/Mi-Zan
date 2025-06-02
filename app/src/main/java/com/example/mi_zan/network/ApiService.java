package com.example.mi_zan.network;

import com.example.mi_zan.model.JadwalResponse;
import com.example.mi_zan.model.LokasiResponse; // Pastikan ini adalah model yang benar (List<LokasiItem>)

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("sholat/kota/cari/{keyword}")
    Call<LokasiResponse> searchKota(@Path("keyword") String keyword);

    // Metode baru untuk mengambil semua kota
    @GET("sholat/kota/semua")
    Call<LokasiResponse> getAllKota(); // Endpoint ini biasanya mengembalikan list langsung,
    // kita asumsikan strukturnya sama dengan LokasiResponse
    // jika tidak, buat model baru misal AllLokasiResponse

    @GET("sholat/jadwal/{idkota}/{tahun}/{bulan}")
    Call<JadwalResponse> getJadwalSholat(
            @Path("idkota") String idKota,
            @Path("tahun") String tahun,
            @Path("bulan") String bulan
    );
}