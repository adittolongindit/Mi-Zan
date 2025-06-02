package com.example.mi_zan.api.waktusholat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WaktuSholatApiService {
    // Endpoint tetap sama, hanya nama service dan model yang berubah
    @GET("sholat/format/json/kota/nama/{nama_kota}")
    Call<WaktuSholatKotaResponseApi> getKotaByName(@Path("nama_kota") String namaKota);

    @GET("sholat/format/json/jadwal/kota/{kode_kota}/tanggal/{tanggal}")
    Call<DataJadwalSholatResponseApi> getJadwalSholat(
            @Path("kode_kota") String kodeKota,
            @Path("tanggal") String tanggal // Format YYYY-MM-DD
    );
}