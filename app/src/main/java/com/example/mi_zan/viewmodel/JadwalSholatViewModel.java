package com.example.mi_zan.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// Model dan Service untuk MyQuran API
import com.example.mi_zan.api.myquran.MyQuranApiResponse;
import com.example.mi_zan.api.myquran.MyQuranApiService;
import com.example.mi_zan.api.myquran.MyQuranApiRetrofitClient;
import com.example.mi_zan.api.myquran.MyQuranDataObject;
import com.example.mi_zan.api.myquran.MyQuranJadwalActual;

// Model dan Service untuk Wilayah ID API
import com.example.mi_zan.api.wilayah.WilayahIdApiService;
import com.example.mi_zan.api.wilayah.WilayahIdRetrofitClient;
import com.example.mi_zan.model.RegionItem;
import com.example.mi_zan.api.wilayah.WilayahIdDataItem;
import com.example.mi_zan.api.wilayah.WilayahIdProvincesResponse;
import com.example.mi_zan.api.wilayah.WilayahIdRegenciesResponse;

// Model UI
import com.example.mi_zan.model.WaktuSholatItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalSholatViewModel extends ViewModel {
    private static final String TAG = "JadwalSholatVM";
    // Ganti dengan ID kota default untuk api.myquran.com, misal "1632" untuk Kota Kediri
    // Anda perlu mencari cara untuk mendapatkan ID ini jika ingin dinamis,
    // karena API MyQuran tidak menyediakan pencarian ID berdasarkan nama kota.
    private static final String DEFAULT_MYQURAN_CITY_ID = "1632";

    private final WilayahIdApiService wilayahIdApiService;
    private final MyQuranApiService myQuranApiService; // Menggunakan service API MyQuran

    private final MutableLiveData<List<RegionItem>> provinsi = new MutableLiveData<>();
    private final MutableLiveData<List<RegionItem>> kota = new MutableLiveData<>();
    private final MutableLiveData<List<String>> bulan = new MutableLiveData<>();
    private final MutableLiveData<List<String>> tahun = new MutableLiveData<>();
    private final MutableLiveData<List<WaktuSholatItem>> jadwalSholat = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> currentLokasiMyQuran = new MutableLiveData<>(); // Untuk lokasi dari MyQuran API

    public final MutableLiveData<RegionItem> selectedProvince = new MutableLiveData<>();
    public final MutableLiveData<RegionItem> selectedCity = new MutableLiveData<>();
    public final MutableLiveData<String> selectedBulanValue = new MutableLiveData<>();
    public final MutableLiveData<String> selectedTahunValue = new MutableLiveData<>();

    public JadwalSholatViewModel() {
        wilayahIdApiService = WilayahIdRetrofitClient.getApiService();
        myQuranApiService = MyQuranApiRetrofitClient.getApiService(); // Inisialisasi service MyQuran
        loadInitialFilterData();
    }

    public LiveData<List<RegionItem>> getProvinces() { return provinsi; }
    public LiveData<List<RegionItem>> getCities() { return kota; }
    public LiveData<List<String>> getMonths() { return bulan; }
    public LiveData<List<String>> getYears() { return tahun; }
    public LiveData<List<WaktuSholatItem>> getPrayerSchedule() { return jadwalSholat; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getCurrentLokasiMyQuran() { return currentLokasiMyQuran; }


    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    private void loadInitialFilterData() {
        List<String> monthNames = Arrays.asList("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember");
        bulan.setValue(monthNames);

        List<String> yearValues = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        selectedTahunValue.setValue(String.valueOf(currentYear));
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearValues.add(String.valueOf(i));
        }
        tahun.setValue(yearValues);

        Calendar calendar = Calendar.getInstance();
        selectedBulanValue.setValue(String.format(Locale.US, "%02d", calendar.get(Calendar.MONTH) + 1));

        fetchProvinces();
        // Langsung fetch jadwal sholat untuk ID default dan tanggal/bulan/tahun saat ini
        fetchPrayerTimesMyQuran();
    }

    public void fetchProvinces() {
        isLoading.setValue(true);
        wilayahIdApiService.getProvinces().enqueue(new Callback<WilayahIdProvincesResponse>() {
            @Override
            public void onResponse(@NonNull Call<WilayahIdProvincesResponse> call, @NonNull Response<WilayahIdProvincesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<RegionItem> provinceItems = new ArrayList<>();
                    for (WilayahIdDataItem item : response.body().getData()) {
                        provinceItems.add(new RegionItem(item.getCode(), item.getName()));
                    }
                    provinsi.setValue(provinceItems);
                    if (!provinceItems.isEmpty()) {
                        if (selectedProvince.getValue() == null || !selectedProvince.getValue().equals(provinceItems.get(0))) {
                            selectedProvince.setValue(provinceItems.get(0));
                        } else {
                            if (kota.getValue() == null || kota.getValue().isEmpty()) {
                                fetchCitiesForProvince(provinceItems.get(0).getId());
                            } else {
                                // isLoading.setValue(false); // Akan dihandle oleh fetchPrayerTimesMyQuran jika ini adalah load awal
                            }
                        }
                    } else {
                        provinsi.setValue(new ArrayList<>());
                        kota.setValue(new ArrayList<>());
                        // jadwalSholat.setValue(new ArrayList<>()); // Jadwal dari MyQuran, tidak terpengaruh langsung
                        isLoading.setValue(false);
                    }
                } else {
                    errorMessage.setValue("Gagal mengambil data provinsi.");
                    Log.e(TAG, "Gagal mengambil provinsi: " + response.code() + " - " + response.message());
                    provinsi.setValue(new ArrayList<>());
                    kota.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WilayahIdProvincesResponse> call, @NonNull Throwable t) {
                errorMessage.setValue("Error jaringan mengambil provinsi: " + t.getMessage());
                Log.e(TAG, "Error API Provinsi WilayahId: ", t);
                provinsi.setValue(new ArrayList<>());
                kota.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }

    public void fetchCitiesForProvince(String provinceId) {
        if (provinceId == null) {
            kota.setValue(new ArrayList<>());
            selectedCity.setValue(null);
            isLoading.setValue(false);
            return;
        }
        isLoading.setValue(true); // Set true karena ini adalah operasi jaringan baru
        wilayahIdApiService.getRegencies(provinceId).enqueue(new Callback<WilayahIdRegenciesResponse>() {
            @Override
            public void onResponse(@NonNull Call<WilayahIdRegenciesResponse> call, @NonNull Response<WilayahIdRegenciesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<RegionItem> cityItems = new ArrayList<>();
                    for (WilayahIdDataItem item : response.body().getData()) {
                        cityItems.add(new RegionItem(item.getCode(), item.getName()));
                    }
                    kota.setValue(cityItems);
                    if (!cityItems.isEmpty()) {
                        if (selectedCity.getValue() == null || !Objects.equals(selectedCity.getValue().getId(), cityItems.get(0).getId())) {
                            selectedCity.setValue(cityItems.get(0));
                        }
                    } else {
                        kota.setValue(new ArrayList<>());
                        selectedCity.setValue(null);
                    }
                } else {
                    errorMessage.setValue("Gagal mengambil data kota.");
                    Log.e(TAG, "Gagal mengambil kota: " + response.code() + " - " + response.message());
                    kota.setValue(new ArrayList<>());
                    selectedCity.setValue(null);
                }
                // isLoading akan dihandle oleh fetchPrayerTimesMyQuran jika ini adalah load awal.
                // Jika ini adalah pemanggilan terpisah, kita mungkin perlu set isLoading false di sini.
                // Untuk saat ini, asumsikan fetchPrayerTimesMyQuran akan selalu dipanggil setelah filter berubah.
                if (isLoading.getValue() != null && isLoading.getValue() && (jadwalSholat.getValue() != null && !jadwalSholat.getValue().isEmpty())) {
                    // Jika jadwal sudah ada, dan ini hanya update kota, matikan loading
                    // isLoading.setValue(false); // Ini bisa terlalu cepat jika fetchPrayerTimesMyQuran belum selesai
                }
                // Jika tidak ada jadwal yang akan di-fetch setelah ini, set isLoading false
                if (selectedBulanValue.getValue() == null || selectedTahunValue.getValue() == null) {
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WilayahIdRegenciesResponse> call, @NonNull Throwable t) {
                errorMessage.setValue("Error jaringan mengambil kota: " + t.getMessage());
                Log.e(TAG, "Error API Kota WilayahId: ", t);
                kota.setValue(new ArrayList<>());
                selectedCity.setValue(null);
                isLoading.setValue(false);
            }
        });
    }

    // Metode untuk mengambil jadwal sholat menggunakan MyQuran API
    public void fetchPrayerTimesMyQuran() {
        String myQuranCityIdToUse = DEFAULT_MYQURAN_CITY_ID; // Gunakan ID default
        String year = selectedTahunValue.getValue();
        String month = selectedBulanValue.getValue();

        if (year == null || month == null) {
            Log.w(TAG, "Data tahun atau bulan tidak lengkap untuk mengambil jadwal sholat MyQuran.");
            // jadwalSholat.setValue(new ArrayList<>()); // Jangan clear jika hanya menunggu
            return;
        }
        isLoading.setValue(true);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1); // Month is 0-indexed

        Calendar today = Calendar.getInstance();
        int dayOfMonth;
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
            dayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        } else {
            dayOfMonth = 1;
        }
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String tanggal = dateFormat.format(calendar.getTime());

        Log.d(TAG, "Mengambil jadwal sholat MyQuran untuk City ID: " + myQuranCityIdToUse + ", Tanggal: " + tanggal);

        myQuranApiService.getJadwalHarian(myQuranCityIdToUse, tanggal).enqueue(new Callback<MyQuranApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyQuranApiResponse> call, @NonNull Response<MyQuranApiResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    MyQuranDataObject dataObject = response.body().getData();
                    if (dataObject != null && dataObject.getJadwal() != null) {
                        MyQuranJadwalActual times = dataObject.getJadwal();
                        currentLokasiMyQuran.setValue(dataObject.getLokasi() + ", " + dataObject.getDaerah());

                        List<WaktuSholatItem> items = new ArrayList<>();
                        items.add(new WaktuSholatItem("Imsak", times.getImsak()));
                        items.add(new WaktuSholatItem("Subuh", times.getSubuh()));
                        items.add(new WaktuSholatItem("Terbit", times.getTerbit()));
                        items.add(new WaktuSholatItem("Dhuha", times.getDhuha()));
                        items.add(new WaktuSholatItem("Dzuhur", times.getDzuhur()));
                        items.add(new WaktuSholatItem("Ashar", times.getAshar()));
                        items.add(new WaktuSholatItem("Maghrib", times.getMaghrib()));
                        items.add(new WaktuSholatItem("Isya", times.getIsya()));
                        jadwalSholat.setValue(items);
                        errorMessage.setValue(null);
                    } else {
                        jadwalSholat.setValue(new ArrayList<>());
                        errorMessage.setValue("Data jadwal tidak ditemukan dalam respons MyQuran.");
                        currentLokasiMyQuran.setValue("Lokasi tidak diketahui");
                    }
                } else {
                    jadwalSholat.setValue(new ArrayList<>());
                    currentLokasiMyQuran.setValue("Gagal memuat lokasi");
                    String errorMsg = "Gagal mengambil jadwal sholat dari MyQuran.";
                    if (response.body() != null && !response.body().isStatus()){
                        errorMsg += " Status API: false.";
                    } else if (response.errorBody() != null) {
                        try { errorMsg += " Detail: " + response.errorBody().string(); } catch (Exception e) { Log.e(TAG, "Error parsing error body", e); }
                    } else if (response.message() != null){
                        errorMsg += " HTTP: " + response.code() + " " + response.message();
                    }
                    errorMessage.setValue(errorMsg);
                    Log.e(TAG, errorMsg + " Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyQuranApiResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                jadwalSholat.setValue(new ArrayList<>());
                currentLokasiMyQuran.setValue("Gagal memuat lokasi");
                errorMessage.setValue("Error jaringan mengambil jadwal MyQuran: " + t.getMessage());
                Log.e(TAG, "Error API Jadwal Sholat MyQuran: ", t);
            }
        });
    }
}