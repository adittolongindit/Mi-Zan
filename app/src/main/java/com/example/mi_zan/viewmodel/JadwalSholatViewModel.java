package com.example.mi_zan.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mi_zan.api.waktusholat.DataJadwalSholatApi;
import com.example.mi_zan.api.waktusholat.DataJadwalSholatResponseApi;
import com.example.mi_zan.api.waktusholat.WaktuJadwalSholatApi;
// import com.example.mi_zan.api.waktusholat.WaktuSholatApiKotaItem; // Tidak terpakai secara langsung di ViewModel
import com.example.mi_zan.api.waktusholat.WaktuSholatApiRetrofitClient;
import com.example.mi_zan.api.waktusholat.WaktuSholatApiService;
import com.example.mi_zan.api.waktusholat.WaktuSholatKotaResponseApi;
import com.example.mi_zan.api.wilayah.WilayahIdApiService;
import com.example.mi_zan.api.wilayah.WilayahIdRetrofitClient;
import com.example.mi_zan.model.RegionItem;
import com.example.mi_zan.model.WaktuSholatItem;
import com.example.mi_zan.api.wilayah.WilayahIdDataItem;
import com.example.mi_zan.api.wilayah.WilayahIdProvincesResponse;
import com.example.mi_zan.api.wilayah.WilayahIdRegenciesResponse;

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

    private final WilayahIdApiService wilayahIdApiService;
    private final WaktuSholatApiService waktuSholatApiService;

    private final MutableLiveData<List<RegionItem>> provinsi = new MutableLiveData<>();
    private final MutableLiveData<List<RegionItem>> kota = new MutableLiveData<>();
    private final MutableLiveData<List<String>> bulan = new MutableLiveData<>();
    private final MutableLiveData<List<String>> tahun = new MutableLiveData<>();
    private final MutableLiveData<List<WaktuSholatItem>> jadwalSholat = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public final MutableLiveData<RegionItem> selectedProvince = new MutableLiveData<>();
    public final MutableLiveData<RegionItem> selectedCity = new MutableLiveData<>();
    private final MutableLiveData<String> selectedWaktuSholatApiCityId = new MutableLiveData<>();
    public final MutableLiveData<String> selectedBulanValue = new MutableLiveData<>();
    public final MutableLiveData<String> selectedTahunValue = new MutableLiveData<>();

    public JadwalSholatViewModel() {
        wilayahIdApiService = WilayahIdRetrofitClient.getApiService();
        waktuSholatApiService = WaktuSholatApiRetrofitClient.getApiService();
        loadInitialFilterData();
    }

    public LiveData<List<RegionItem>> getProvinces() { return provinsi; }
    public LiveData<List<RegionItem>> getCities() { return kota; }
    public LiveData<List<String>> getMonths() { return bulan; }
    public LiveData<List<String>> getYears() { return tahun; }
    public LiveData<List<WaktuSholatItem>> getPrayerSchedule() { return jadwalSholat; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSelectedWaktuSholatApiCityId() { return selectedWaktuSholatApiCityId; }

    // Metode untuk membersihkan pesan error
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
                                isLoading.setValue(false);
                            }
                        }
                    } else {
                        provinsi.setValue(new ArrayList<>());
                        kota.setValue(new ArrayList<>());
                        jadwalSholat.setValue(new ArrayList<>());
                        isLoading.setValue(false);
                    }
                } else {
                    errorMessage.setValue("Gagal mengambil data provinsi.");
                    Log.e(TAG, "Gagal mengambil provinsi: " + response.code() + " - " + response.message());
                    provinsi.setValue(new ArrayList<>());
                    kota.setValue(new ArrayList<>());
                    jadwalSholat.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WilayahIdProvincesResponse> call, @NonNull Throwable t) {
                errorMessage.setValue("Error jaringan mengambil provinsi: " + t.getMessage());
                Log.e(TAG, "Error API Provinsi WilayahId: ", t);
                provinsi.setValue(new ArrayList<>());
                kota.setValue(new ArrayList<>());
                jadwalSholat.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }

    public void fetchCitiesForProvince(String provinceId) {
        if (provinceId == null) {
            kota.setValue(new ArrayList<>());
            selectedCity.setValue(null);
            selectedWaktuSholatApiCityId.setValue(null);
            jadwalSholat.setValue(new ArrayList<>());
            isLoading.setValue(false);
            return;
        }
        isLoading.setValue(true);
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
                        } else {
                            if (selectedWaktuSholatApiCityId.getValue() == null) {
                                fetchPrayerTimeApiCityId(cityItems.get(0).getName());
                            } else {
                                isLoading.setValue(false);
                            }
                        }
                    } else {
                        kota.setValue(new ArrayList<>());
                        selectedCity.setValue(null);
                        selectedWaktuSholatApiCityId.setValue(null);
                        jadwalSholat.setValue(new ArrayList<>());
                        isLoading.setValue(false);
                    }
                } else {
                    errorMessage.setValue("Gagal mengambil data kota.");
                    Log.e(TAG, "Gagal mengambil kota: " + response.code() + " - " + response.message());
                    kota.setValue(new ArrayList<>());
                    selectedCity.setValue(null);
                    selectedWaktuSholatApiCityId.setValue(null);
                    jadwalSholat.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WilayahIdRegenciesResponse> call, @NonNull Throwable t) {
                errorMessage.setValue("Error jaringan mengambil kota: " + t.getMessage());
                Log.e(TAG, "Error API Kota WilayahId: ", t);
                kota.setValue(new ArrayList<>());
                selectedCity.setValue(null);
                selectedWaktuSholatApiCityId.setValue(null);
                jadwalSholat.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }

    public void fetchPrayerTimeApiCityId(String cityName) {
        if (cityName == null) {
            selectedWaktuSholatApiCityId.setValue(null);
            jadwalSholat.setValue(new ArrayList<>());
            isLoading.setValue(false);
            return;
        }
        isLoading.setValue(true);
        waktuSholatApiService.getKotaByName(cityName.toLowerCase()).enqueue(new Callback<WaktuSholatKotaResponseApi>() {
            @Override
            public void onResponse(@NonNull Call<WaktuSholatKotaResponseApi> call, @NonNull Response<WaktuSholatKotaResponseApi> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getKota() != null && !response.body().getKota().isEmpty()) {
                    String newCityId = response.body().getKota().get(0).getId();
                    if (!Objects.equals(selectedWaktuSholatApiCityId.getValue(), newCityId)) {
                        selectedWaktuSholatApiCityId.setValue(newCityId);
                    } else {
                        if (jadwalSholat.getValue() == null || jadwalSholat.getValue().isEmpty()) {
                            fetchPrayerTimes();
                        } else {
                            isLoading.setValue(false);
                        }
                    }
                } else {
                    errorMessage.setValue("Kode kota (API Jadwal) tidak ditemukan untuk: " + cityName);
                    Log.e(TAG, "Gagal mengambil kode kota (API Jadwal): " + response.code() + " - " + response.message() + " untuk " + cityName);
                    selectedWaktuSholatApiCityId.setValue(null);
                    jadwalSholat.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WaktuSholatKotaResponseApi> call, @NonNull Throwable t) {
                errorMessage.setValue("Error jaringan mengambil kode kota (API Jadwal): " + t.getMessage());
                Log.e(TAG, "Error API Kode Kota (API Jadwal): ", t);
                selectedWaktuSholatApiCityId.setValue(null);
                jadwalSholat.setValue(new ArrayList<>());
                isLoading.setValue(false);
            }
        });
    }
    public void fetchPrayerTimes() {
        String cityId = selectedWaktuSholatApiCityId.getValue();
        String year = selectedTahunValue.getValue();
        String month = selectedBulanValue.getValue();

        if (cityId == null || year == null || month == null) {
            Log.w(TAG, "Data filter tidak lengkap untuk mengambil jadwal sholat. CityID: " + cityId + ", Year: " + year + ", Month: " + month);
            jadwalSholat.setValue(new ArrayList<>());
            return;
        }
        isLoading.setValue(true);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        Calendar today = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
            calendar.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH));
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String tanggal = dateFormat.format(calendar.getTime());

        Log.d(TAG, "Mengambil jadwal sholat untuk City ID: " + cityId + ", Tanggal: " + tanggal);

        waktuSholatApiService.getJadwalSholat(cityId, tanggal).enqueue(new Callback<DataJadwalSholatResponseApi>() {
            @Override
            public void onResponse(@NonNull Call<DataJadwalSholatResponseApi> call, @NonNull Response<DataJadwalSholatResponseApi> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && "ok".equals(response.body().getStatus()) && response.body().getJadwal() != null && "data berhasil diambil".equals(response.body().getJadwal().getPesan())) {
                    WaktuJadwalSholatApi times = response.body().getJadwal().getData();
                    if (times != null) {
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
                        errorMessage.setValue("Data jadwal tidak ditemukan dalam respons.");
                    }
                } else {
                    jadwalSholat.setValue(new ArrayList<>());
                    String errorMsg = "Gagal mengambil jadwal sholat.";
                    if (response.body() != null && response.body().getJadwal() != null && response.body().getJadwal().getPesan() != null) {
                        errorMsg += " Pesan API: " + response.body().getJadwal().getPesan();
                    } else if (response.body() != null && response.body().getStatus() != null){
                        errorMsg += " Status API: " + response.body().getStatus();
                    } else if (response.errorBody() != null) {
                        try { errorMsg += " Detail: " + response.errorBody().string(); } catch (Exception e) { Log.e(TAG, "Error parsing error body", e); }
                    } else if (response.message() != null){ errorMsg += " HTTP: " + response.code() + " " + response.message(); }
                    errorMessage.setValue(errorMsg);
                    Log.e(TAG, errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataJadwalSholatResponseApi> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                jadwalSholat.setValue(new ArrayList<>());
                errorMessage.setValue("Error jaringan mengambil jadwal: " + t.getMessage());
                Log.e(TAG, "Error API Jadwal Sholat: ", t);
            }
        });
    }
}