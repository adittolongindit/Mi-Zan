package com.example.mi_zan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mi_zan.adapter.JadwalSholatAdapter;
import com.example.mi_zan.model.JadwalData;
import com.example.mi_zan.model.JadwalItem;
import com.example.mi_zan.model.JadwalResponse;
import com.example.mi_zan.model.LokasiItem;
import com.example.mi_zan.model.LokasiResponse;
import com.example.mi_zan.model.SinglePrayerTime;
import com.example.mi_zan.network.ApiService;
import com.example.mi_zan.network.RetrofitClient;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalSholatFragment extends Fragment {

    private static final String TAG = "JadwalSholatFragment";

    private EditText etSearchKota;
    private Button btnSearchKota;
    private Spinner spinnerKota, spinnerBulan, spinnerTahun;
    private Button btnTampilkanJadwal;
    private RecyclerView rvJadwalSholat;
    private TextView tvCurrentLocation;
    private ImageButton btnToggleFilter;
    private LinearLayout layoutFilterContent;

    private ApiService apiService;
    private JadwalSholatAdapter jadwalSholatAdapter;
    private List<LokasiItem> lokasiItemList = new ArrayList<>();
    private List<SinglePrayerTime> singlePrayerTimeDisplayList = new ArrayList<>();
    private ArrayAdapter<LokasiItem> kotaAdapter;
    private LokasiItem selectedLokasi;

    private boolean isFilterVisible = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jadwal_sholat_fragment, container, false);

        etSearchKota = view.findViewById(R.id.et_search_kota);
        btnSearchKota = view.findViewById(R.id.btn_search_kota);
        spinnerKota = view.findViewById(R.id.spinner_kota);
        spinnerBulan = view.findViewById(R.id.spinner_bulan);
        spinnerTahun = view.findViewById(R.id.spinner_tahun);
        btnTampilkanJadwal = view.findViewById(R.id.btn_tampilkan_jadwal);
        rvJadwalSholat = view.findViewById(R.id.rv_jadwal_sholat);
        tvCurrentLocation = view.findViewById(R.id.tv_current_location_myquran);
        btnToggleFilter = view.findViewById(R.id.btn_toggle_filter);
        layoutFilterContent = view.findViewById(R.id.layout_filter_content);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        setupFilterToggle();
        setupSpinners();
        setupRecyclerView();

        btnSearchKota.setOnClickListener(v -> {
            String keyword = etSearchKota.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(getContext(), "Memuat semua kota...", Toast.LENGTH_SHORT).show();
                fetchAllKota();
            } else {
                searchKotaByKeyword(keyword);
            }
        });

        btnTampilkanJadwal.setOnClickListener(v -> fetchJadwalSholat());

        fetchAllKota();

        return view;
    }

    private void setupFilterToggle() {
        layoutFilterContent.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);

        btnToggleFilter.setOnClickListener(v -> {
            isFilterVisible = !isFilterVisible;
            layoutFilterContent.setVisibility(isFilterVisible ? View.VISIBLE : View.GONE);
            Toast.makeText(getContext(), isFilterVisible ? "Filter ditampilkan" : "Filter disembunyikan", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSpinners() {
        kotaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, lokasiItemList);
        kotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKota.setAdapter(kotaAdapter);
        spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!lokasiItemList.isEmpty() && position < lokasiItemList.size()) {
                    selectedLokasi = lokasiItemList.get(position);
                } else {
                    selectedLokasi = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLokasi = null;
            }
        });

        List<String> bulanList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            bulanList.add(String.format(Locale.getDefault(), "%02d", i));
        }
        ArrayAdapter<String> bulanAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, bulanList);
        bulanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBulan.setAdapter(bulanAdapter);
        Calendar calendar = Calendar.getInstance();
        int currentMonthIndex = calendar.get(Calendar.MONTH);
        spinnerBulan.setSelection(currentMonthIndex);

        List<String> tahunList = new ArrayList<>();
        int currentYear = calendar.get(Calendar.YEAR);
        for (int i = currentYear - 2; i <= currentYear + 5; i++) {
            tahunList.add(String.valueOf(i));
        }
        ArrayAdapter<String> tahunAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tahunList);
        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTahun.setAdapter(tahunAdapter);
        spinnerTahun.setSelection(tahunList.indexOf(String.valueOf(currentYear)));
    }

    private void setupRecyclerView() {
        jadwalSholatAdapter = new JadwalSholatAdapter(getContext(), singlePrayerTimeDisplayList);
        rvJadwalSholat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJadwalSholat.setAdapter(jadwalSholatAdapter);
    }

    private void fetchAllKota() {
        tvCurrentLocation.setText("Memuat daftar semua kota...");
        tvCurrentLocation.setVisibility(View.VISIBLE);
        apiService.getAllKota().enqueue(new Callback<LokasiResponse>() {
            @Override
            public void onResponse(@NonNull Call<LokasiResponse> call, @NonNull Response<LokasiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    lokasiItemList.clear();
                    if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                        lokasiItemList.addAll(response.body().getData());
                        kotaAdapter.notifyDataSetChanged();
                        if (!lokasiItemList.isEmpty()) {
                            spinnerKota.setSelection(0);
                            selectedLokasi = lokasiItemList.get(0);
                        }
                        tvCurrentLocation.setText("Pilih kota dari daftar.");
                        Toast.makeText(getContext(), "Daftar semua kota dimuat", Toast.LENGTH_SHORT).show();
                    } else {
                        tvCurrentLocation.setText("Tidak ada data kota yang ditemukan.");
                        kotaAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Tidak ada kota yang bisa dimuat", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tvCurrentLocation.setText("Gagal memuat daftar kota.");
                    Toast.makeText(getContext(), "Gagal memuat daftar kota: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Fetch All Kota Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LokasiResponse> call, @NonNull Throwable t) {
                tvCurrentLocation.setText("Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error memuat daftar kota: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Fetch All Kota Failure: ", t);
            }
        });
    }

    private void searchKotaByKeyword(String keyword) {
        tvCurrentLocation.setText("Mencari lokasi '" + keyword + "'...");
        tvCurrentLocation.setVisibility(View.VISIBLE);
        apiService.searchKota(keyword).enqueue(new Callback<LokasiResponse>() {
            @Override
            public void onResponse(@NonNull Call<LokasiResponse> call, @NonNull Response<LokasiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    lokasiItemList.clear();
                    if (response.body().getData() != null && !response.body().getData().isEmpty()) {
                        lokasiItemList.addAll(response.body().getData());
                        kotaAdapter.notifyDataSetChanged();
                        if (!lokasiItemList.isEmpty()) {
                            spinnerKota.setSelection(0);
                            selectedLokasi = lokasiItemList.get(0);
                        }
                        tvCurrentLocation.setText("Pilih lokasi dari hasil pencarian.");
                        Toast.makeText(getContext(), "Pilih kota dari daftar hasil pencarian", Toast.LENGTH_SHORT).show();
                    } else {
                        tvCurrentLocation.setText("Lokasi '" + keyword + "' tidak ditemukan.");
                        kotaAdapter.notifyDataSetChanged(); // Kosongkan spinner jika tidak ada hasil
                        Toast.makeText(getContext(), "Kota tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    tvCurrentLocation.setText("Gagal mencari lokasi.");
                    Toast.makeText(getContext(), "Gagal mencari kota: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Search Kota Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LokasiResponse> call, @NonNull Throwable t) {
                tvCurrentLocation.setText("Error pencarian: " + t.getMessage());
                Toast.makeText(getContext(), "Error pencarian: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Search Kota Failure: ", t);
            }
        });
    }

    private void fetchJadwalSholat() {
        if (selectedLokasi == null) {
            Toast.makeText(getContext(), "Silakan pilih kota terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String idKota = selectedLokasi.getId();
        String bulan = spinnerBulan.getSelectedItem().toString();
        String tahun = spinnerTahun.getSelectedItem().toString();

        if (idKota.isEmpty() || bulan.isEmpty() || tahun.isEmpty()) {
            Toast.makeText(getContext(), "Pastikan semua filter terisi", Toast.LENGTH_SHORT).show();
            return;
        }

        tvCurrentLocation.setText("Memuat jadwal untuk " + selectedLokasi.getLokasi() + "...");
        tvCurrentLocation.setVisibility(View.VISIBLE);
        Log.d(TAG, "Fetching jadwal: ID=" + idKota + ", Tahun=" + tahun + ", Bulan=" + bulan);

        apiService.getJadwalSholat(idKota, tahun, bulan).enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(@NonNull Call<JadwalResponse> call, @NonNull Response<JadwalResponse> response) {
                singlePrayerTimeDisplayList.clear();

                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    JadwalData jadwalData = response.body().getData();

                    if (jadwalData != null && jadwalData.getJadwal() != null && !jadwalData.getJadwal().isEmpty()) {
                        for (JadwalItem dailySchedule : jadwalData.getJadwal()) {
                            String tanggalDisplay = dailySchedule.getTanggal();
                            String rawDate = dailySchedule.getDate();

                            if (dailySchedule.getImsak() != null && !dailySchedule.getImsak().isEmpty()) {
                                singlePrayerTimeDisplayList.add(new SinglePrayerTime("Imsak", dailySchedule.getImsak(), tanggalDisplay, rawDate));
                            }
                            if (dailySchedule.getSubuh() != null && !dailySchedule.getSubuh().isEmpty()) {
                                singlePrayerTimeDisplayList.add(new SinglePrayerTime("Subuh", dailySchedule.getSubuh(), tanggalDisplay, rawDate));
                            }
                            if (dailySchedule.getTerbit() != null && !dailySchedule.getTerbit().isEmpty()) {
                                singlePrayerTimeDisplayList.add(new SinglePrayerTime("Terbit", dailySchedule.getTerbit(), tanggalDisplay, rawDate));
                            }
                            if (dailySchedule.getDhuha() != null && !dailySchedule.getDhuha().isEmpty()) {
                                singlePrayerTimeDisplayList.add(new SinglePrayerTime("Dhuha", dailySchedule.getDhuha(), tanggalDisplay, rawDate));
                            }
                            if (dailySchedule.getDzuhur() != null && !dailySchedule.getDzuhur().isEmpty()) {
                                singlePrayerTimeDisplayList.add(new SinglePrayerTime("Dzuhur", dailySchedule.getDzuhur(), tanggalDisplay, rawDate));
                            }
                            if (dailySchedule.getAshar() != null && !dailySchedule.getAshar().isEmpty()) {
                                singlePrayerTimeDisplayList.add(new SinglePrayerTime("Ashar", dailySchedule.getAshar(), tanggalDisplay, rawDate));
                            }
                            if (dailySchedule.getMaghrib() != null && !dailySchedule.getMaghrib().isEmpty()) {
                                singlePrayerTimeDisplayList.add(new SinglePrayerTime("Maghrib", dailySchedule.getMaghrib(), tanggalDisplay, rawDate));
                            }
                            if (dailySchedule.getIsya() != null && !dailySchedule.getIsya().isEmpty()) {
                                singlePrayerTimeDisplayList.add(new SinglePrayerTime("Isya", dailySchedule.getIsya(), tanggalDisplay, rawDate));
                            }
                        }
                        jadwalSholatAdapter.notifyDataSetChanged();
                        tvCurrentLocation.setText("Jadwal Sholat untuk: " + jadwalData.getLokasi() + " (" + jadwalData.getDaerah() + ")");
                        Toast.makeText(getContext(), "Jadwal dimuat", Toast.LENGTH_SHORT).show();
                    } else {
                        jadwalSholatAdapter.notifyDataSetChanged();
                        tvCurrentLocation.setText("Data jadwal tidak ditemukan untuk " + selectedLokasi.getLokasi());
                        Toast.makeText(getContext(), "Data jadwal kosong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    jadwalSholatAdapter.notifyDataSetChanged();
                    tvCurrentLocation.setText("Gagal memuat jadwal untuk " + selectedLokasi.getLokasi());
                    Toast.makeText(getContext(), "Gagal memuat jadwal: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Fetch Jadwal Error: " + response.code() + " - " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error Body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JadwalResponse> call, @NonNull Throwable t) {
                singlePrayerTimeDisplayList.clear();
                jadwalSholatAdapter.notifyDataSetChanged();
                tvCurrentLocation.setText("Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fetch Jadwal Failure: ", t);
            }
        });
    }
}