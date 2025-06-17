package com.example.mi_zan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mi_zan.adapter.KotaAdapter;
import com.example.mi_zan.adapter.JadwalHarianAdapter;
import com.example.mi_zan.adapter.JadwalBulananAdapter;
import com.example.mi_zan.model.JadwalItem;
import com.example.mi_zan.model.JadwalResponse;
import com.example.mi_zan.model.LokasiItem;
import com.example.mi_zan.model.LokasiResponse;
import com.example.mi_zan.model.WaktuSholatItem;
import com.example.mi_zan.network.ApiService;
import com.example.mi_zan.network.RetrofitClient;
import com.example.mi_zan.util.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalSholatFragment extends Fragment {

    private static final String TAG = "JadwalSholatFragment";

    private enum ViewState { HOME, CITY_SELECTION, MONTHLY_SCHEDULE }
    private ViewState currentViewState = ViewState.HOME;

    // Common
    private ApiService apiService;
    private SessionManager sessionManager;
    private LottieAnimationView loadingAnimationView; // PERBAIKAN: Menggunakan Lottie

    // Home View
    private CoordinatorLayout homeViewContainer;
    private TextView tvCountdown, tvNextPrayer, tvCurrentDate;
    private Button btnChangeLocationHome, btnMonthlySchedule;
    private RecyclerView rvJadwalHarian;
    private CountDownTimer countDownTimer;

    // City Selection View
    private LinearLayout citySelectionContainer;
    private KotaAdapter cityAdapter; // PERBAIKAN: Nama variabel dan kelas yang konsisten
    private RecyclerView rvCities;
    private SearchView searchViewCity;
    private List<LokasiItem> allCities = new ArrayList<>();

    // Monthly Schedule View
    private LinearLayout monthlyScheduleContainer;
    private RecyclerView rvJadwalBulanan;
    private JadwalBulananAdapter monthlyAdapter;

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(getContext(), "Izin lokasi ditolak. Menampilkan lokasi default.", Toast.LENGTH_LONG).show();
                    fetchJadwalForToday();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.jadwal_sholat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(requireContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        initViews(view);
        setupBackButtonHandler();

        checkLocationPermissionAndGetData();
    }

    private void initViews(View view) {
        loadingAnimationView = view.findViewById(R.id.loading_animation_view); // PERBAIKAN: Inisialisasi Lottie

        homeViewContainer = view.findViewById(R.id.home_view_container);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        tvNextPrayer = view.findViewById(R.id.tv_next_prayer);
        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        btnChangeLocationHome = view.findViewById(R.id.btn_change_location_home);
        btnMonthlySchedule = view.findViewById(R.id.btn_monthly_schedule);
        rvJadwalHarian = view.findViewById(R.id.rv_jadwal_harian);
        rvJadwalHarian.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJadwalHarian.setNestedScrollingEnabled(false);
        btnChangeLocationHome.setOnClickListener(v -> switchToView(ViewState.CITY_SELECTION));
        btnMonthlySchedule.setOnClickListener(v -> switchToView(ViewState.MONTHLY_SCHEDULE));

        citySelectionContainer = view.findViewById(R.id.city_selection_container);
        rvCities = view.findViewById(R.id.rv_cities);
        rvCities.setLayoutManager(new LinearLayoutManager(getContext()));
        searchViewCity = view.findViewById(R.id.search_view_city);
        ((MaterialToolbar) view.findViewById(R.id.toolbar_city_selection)).setNavigationOnClickListener(v -> switchToView(ViewState.HOME));

        monthlyScheduleContainer = view.findViewById(R.id.monthly_schedule_container);
        rvJadwalBulanan = view.findViewById(R.id.rv_jadwal_bulanan);
        rvJadwalBulanan.setLayoutManager(new LinearLayoutManager(getContext()));
        ((MaterialToolbar) view.findViewById(R.id.toolbar_monthly)).setNavigationOnClickListener(v -> switchToView(ViewState.HOME));
    }

    private void switchToView(ViewState newState) {
        if (countDownTimer != null) countDownTimer.cancel();
        currentViewState = newState;

        homeViewContainer.setVisibility(newState == ViewState.HOME ? View.VISIBLE : View.GONE);
        citySelectionContainer.setVisibility(newState == ViewState.CITY_SELECTION ? View.VISIBLE : View.GONE);
        monthlyScheduleContainer.setVisibility(newState == ViewState.MONTHLY_SCHEDULE ? View.VISIBLE : View.GONE);

        switch (newState) {
            case HOME:
                fetchJadwalForToday();
                break;
            case CITY_SELECTION:
                setupCitySelectionView();
                break;
            case MONTHLY_SCHEDULE:
                setupMonthlyScheduleView();
                break;
        }
    }

    private void setupBackButtonHandler() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (currentViewState != ViewState.HOME) {
                    switchToView(ViewState.HOME);
                } else {
                    setEnabled(false);
                    if(isAdded()) requireActivity().onBackPressed();
                }
            }
        });
    }

    private void checkLocationPermissionAndGetData() {
        if (getContext() != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentLocation() {
        if (getContext() == null || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        loadingAnimationView.setVisibility(View.VISIBLE);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        String cityName = addresses.get(0).getSubAdminArea();
                        if (cityName != null) {
                            findCityIdAndFetchSchedule(cityName.replace("Kabupaten ", "").replace("Kota ", ""));
                        } else {
                            fallbackToDefaultLocation("Tidak bisa mendapatkan nama kota dari GPS.");
                        }
                    } else {
                        fallbackToDefaultLocation("Tidak bisa mendapatkan alamat dari GPS.");
                    }
                } catch (IOException e) {
                    fallbackToDefaultLocation("Error Geocoder.");
                }
            } else {
                fallbackToDefaultLocation("Tidak bisa mendapatkan lokasi GPS.");
            }
        }).addOnFailureListener(e -> fallbackToDefaultLocation("Error mendapatkan lokasi GPS."));
    }

    private void findCityIdAndFetchSchedule(String cityName) {
        apiService.searchKota(cityName).enqueue(new Callback<LokasiResponse>() {
            @Override
            public void onResponse(@NonNull Call<LokasiResponse> call, @NonNull Response<LokasiResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().isStatus() && !response.body().getData().isEmpty()) {
                    LokasiItem foundCity = response.body().getData().get(0);
                    sessionManager.saveLocation(foundCity.getId(), foundCity.getLokasi());
                } else if(isAdded()) {
                    Toast.makeText(getContext(), "Kota '" + cityName + "' tidak ditemukan di API. Menggunakan lokasi default.", Toast.LENGTH_LONG).show();
                }
                switchToView(ViewState.HOME);
            }
            @Override
            public void onFailure(@NonNull Call<LokasiResponse> call, @NonNull Throwable t) {
                if(isAdded()) {
                    Toast.makeText(getContext(), "Lokasi gagal didapatkan. Menggunakan lokasi default.", Toast.LENGTH_LONG).show();
                    switchToView(ViewState.HOME);
                }
            }
        });
    }

    private void fallbackToDefaultLocation(String message) {
        if(isAdded()) Toast.makeText(getContext(), message + " Menampilkan lokasi default.", Toast.LENGTH_LONG).show();
        switchToView(ViewState.HOME);
    }

    private void fetchJadwalForToday() {
        loadingAnimationView.setVisibility(View.VISIBLE);
        if (countDownTimer != null) countDownTimer.cancel();

        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        String idKota = sessionManager.getSavedLocationId();
        String tahun = String.valueOf(today.get(Calendar.YEAR));
        String bulan = String.format(Locale.getDefault(), "%02d", today.get(Calendar.MONTH) + 1);

        apiService.getJadwalSholat(idKota, tahun, bulan).enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(@NonNull Call<JadwalResponse> call, @NonNull Response<JadwalResponse> response) {
                if(!isAdded()) return;
                loadingAnimationView.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    JadwalItem todaySchedule = findTodaySchedule(response.body().getData().getJadwal());
                    if (todaySchedule != null) updateHomeUI(todaySchedule);
                } else {
                    Toast.makeText(getContext(), "Gagal memuat jadwal harian.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<JadwalResponse> call, @NonNull Throwable t) {
                if(!isAdded()) return;
                loadingAnimationView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private JadwalItem findTodaySchedule(List<JadwalItem> monthlySchedule) {
        if(monthlySchedule == null) return null;
        Calendar todayCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        String todayDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(todayCal.getTime());
        for (JadwalItem item : monthlySchedule) {
            if (item.getDate().equals(todayDateString)) return item;
        }
        return null;
    }

    private void startCountdown(List<WaktuSholatItem> prayerTimes) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long now = System.currentTimeMillis();
        long nextPrayerTimeMillis = 0;
        String nextPrayerName = "N/A";
        WaktuSholatItem nextPrayerItem = null;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));

        // Cari jadwal sholat berikutnya di hari ini
        for (WaktuSholatItem prayer : prayerTimes) {
            if (prayer.getName().equalsIgnoreCase("Imsak") || prayer.getName().equalsIgnoreCase("Terbit")) continue;
            try {
                Date prayerDate = sdf.parse(prayer.getTime());
                Calendar prayerCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
                prayerCal.set(Calendar.HOUR_OF_DAY, prayerDate.getHours());
                prayerCal.set(Calendar.MINUTE, prayerDate.getMinutes());
                prayerCal.set(Calendar.SECOND, 0);

                if (prayerCal.getTimeInMillis() > now) {
                    nextPrayerTimeMillis = prayerCal.getTimeInMillis();
                    nextPrayerName = prayer.getName() + " " + prayer.getTime();
                    nextPrayerItem = prayer;
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // PERBAIKAN: Logika jika semua jadwal hari ini sudah lewat
        if (nextPrayerItem == null) {
            // Ambil jadwal Subuh dari daftar (biasanya di indeks ke-1)
            if (prayerTimes.size() > 1) {
                WaktuSholatItem subuhBesok = prayerTimes.get(1); // Index 0:Imsak, 1:Subuh
                try {
                    Date prayerDate = sdf.parse(subuhBesok.getTime());
                    Calendar prayerCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
                    // Tambahkan 1 hari untuk menjadikannya jadwal besok
                    prayerCal.add(Calendar.DAY_OF_YEAR, 1);
                    prayerCal.set(Calendar.HOUR_OF_DAY, prayerDate.getHours());
                    prayerCal.set(Calendar.MINUTE, prayerDate.getMinutes());
                    prayerCal.set(Calendar.SECOND, 0);

                    nextPrayerTimeMillis = prayerCal.getTimeInMillis();
                    nextPrayerName = "Subuh " + subuhBesok.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        // Jika berhasil menemukan jadwal (baik hari ini atau besok), mulai countdown
        if (nextPrayerTimeMillis > 0) {
            tvNextPrayer.setText(nextPrayerName);
            long diff = nextPrayerTimeMillis - now;
            countDownTimer = new CountDownTimer(diff, 1000) {
                public void onTick(long millisUntilFinished) {
                    long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                    tvCountdown.setText(String.format(Locale.getDefault(), "%02d : %02d : %02d Menuju", hours, minutes, seconds));
                }
                public void onFinish() {
                    tvCountdown.setText("Waktu Sholat Telah Tiba");
                    fetchJadwalForToday();
                }
            }.start();
        } else {
            // Fallback jika tetap tidak ditemukan
            tvNextPrayer.setText("Jadwal Berikutnya");
            tvCountdown.setText("-- : -- : --");
        }
    }

    // Pastikan pemanggilan JadwalHarianAdapter di method updateHomeUI sudah benar
    private void updateHomeUI(JadwalItem todaySchedule) {
        tvCurrentDate.setText(todaySchedule.getTanggal());
        btnChangeLocationHome.setText(sessionManager.getSavedLocationName());

        List<WaktuSholatItem> dailyPrayerTimes = new ArrayList<>();
        dailyPrayerTimes.add(new WaktuSholatItem("Imsak", todaySchedule.getImsak()));
        dailyPrayerTimes.add(new WaktuSholatItem("Subuh", todaySchedule.getSubuh()));
        dailyPrayerTimes.add(new WaktuSholatItem("Terbit", todaySchedule.getTerbit()));
        dailyPrayerTimes.add(new WaktuSholatItem("Dhuha", todaySchedule.getDhuha()));
        dailyPrayerTimes.add(new WaktuSholatItem("Dzuhur", todaySchedule.getDzuhur()));
        dailyPrayerTimes.add(new WaktuSholatItem("Ashar", todaySchedule.getAshar()));
        dailyPrayerTimes.add(new WaktuSholatItem("Maghrib", todaySchedule.getMaghrib()));
        dailyPrayerTimes.add(new WaktuSholatItem("Isya", todaySchedule.getIsya()));

        // PERBAIKAN PENTING: Pastikan Anda mengirim semua parameter yang dibutuhkan adapter
        rvJadwalHarian.setAdapter(new JadwalHarianAdapter(dailyPrayerTimes, todaySchedule, getContext()));

        startCountdown(dailyPrayerTimes);
    }

    private void setupCitySelectionView() {
        cityAdapter = new KotaAdapter(allCities, kota -> {
            sessionManager.saveLocation(kota.getId(), kota.getLokasi());
            Toast.makeText(getContext(), "Lokasi diubah ke: " + kota.getLokasi(), Toast.LENGTH_SHORT).show();
            switchToView(ViewState.HOME);
        });
        rvCities.setAdapter(cityAdapter);
        searchViewCity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                if (cityAdapter != null) cityAdapter.filter(newText);
                return true;
            }
        });
        if (allCities.isEmpty()) fetchAllCities();
    }

    private void fetchAllCities() {
        loadingAnimationView.setVisibility(View.VISIBLE);
        apiService.getAllKota().enqueue(new Callback<LokasiResponse>() {
            @Override
            public void onResponse(@NonNull Call<LokasiResponse> call, @NonNull Response<LokasiResponse> response) {
                if(!isAdded()) return;
                loadingAnimationView.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    allCities.clear();
                    allCities.addAll(response.body().getData());
                    if (cityAdapter != null) cityAdapter.updateData(allCities);
                } else {
                    Toast.makeText(getContext(), "Gagal memuat daftar kota.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<LokasiResponse> call, @NonNull Throwable t) {
                if(!isAdded()) return;
                loadingAnimationView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMonthlyScheduleView() {
        monthlyAdapter = new JadwalBulananAdapter(new ArrayList<>(), getContext());
        rvJadwalBulanan.setAdapter(monthlyAdapter);
        fetchJadwalForMonth();
    }

    private void fetchJadwalForMonth() {
        loadingAnimationView.setVisibility(View.VISIBLE);
        String idKota = sessionManager.getSavedLocationId();
        Calendar today = Calendar.getInstance();
        String tahun = String.valueOf(today.get(Calendar.YEAR));
        String bulan = String.format(Locale.getDefault(), "%02d", today.get(Calendar.MONTH) + 1);

        apiService.getJadwalSholat(idKota, tahun, bulan).enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(@NonNull Call<JadwalResponse> call, @NonNull Response<JadwalResponse> response) {
                if(!isAdded()) return;
                loadingAnimationView.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    if (monthlyAdapter != null) monthlyAdapter.updateData(response.body().getData().getJadwal());
                } else {
                    Toast.makeText(getContext(), "Gagal memuat jadwal bulanan.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<JadwalResponse> call, @NonNull Throwable t) {
                if(!isAdded()) return;
                loadingAnimationView.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}