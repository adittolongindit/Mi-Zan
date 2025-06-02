package com.example.mi_zan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton; // Import ImageButton
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.Spinner;
import android.widget.Toast;
// import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_zan.adapter.JadwalSholatAdapter;
import com.example.mi_zan.model.RegionItem;
import com.example.mi_zan.model.WaktuSholatItem;
import com.example.mi_zan.viewmodel.JadwalSholatViewModel;

import java.util.ArrayList;
// import java.util.Calendar;
import java.util.List;
import java.util.Locale;
// import java.util.Objects; // Tidak terpakai secara eksplisit di sini

public class JadwalSholatFragment extends Fragment implements JadwalSholatAdapter.OnItemButtonClickListener {

    private JadwalSholatViewModel viewModel;
    private Spinner spinnerProvinsi;
    private Spinner spinnerKota;
    private Spinner spinnerBulan;
    private Spinner spinnerTahun;
    private RecyclerView rvJadwalSholat;
    private JadwalSholatAdapter jadwalSholatAdapter;
    // private ProgressBar progressBar;

    private ArrayAdapter<RegionItem> provinsiAdapter;
    private ArrayAdapter<RegionItem> kotaAdapter;
    private ArrayAdapter<String> bulanAdapter;
    private ArrayAdapter<String> tahunAdapter;

    private LinearLayout layoutFilterContent; // Untuk expand/collapse
    private ImageButton btnToggleFilter; // Tombol expand/collapse
    private boolean isFilterExpanded = true; // State awal filter


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jadwal_sholat_fragment, container, false);
        spinnerProvinsi = view.findViewById(R.id.spinner_provinsi);
        spinnerKota = view.findViewById(R.id.spinner_kota);
        spinnerBulan = view.findViewById(R.id.spinner_bulan);
        spinnerTahun = view.findViewById(R.id.spinner_tahun);
        rvJadwalSholat = view.findViewById(R.id.rv_jadwal_sholat);

        layoutFilterContent = view.findViewById(R.id.layout_filter_content); // ID dari LinearLayout filter
        btnToggleFilter = view.findViewById(R.id.btn_toggle_filter); // ID dari ImageButton expand/collapse

        // progressBar = view.findViewById(R.id.progressBar);
        setupRecyclerView();
        setupFilterToggle(); // Panggil setup untuk tombol expand/collapse
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(JadwalSholatViewModel.class);
        setupSpinners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        jadwalSholatAdapter = new JadwalSholatAdapter();
        jadwalSholatAdapter.setOnItemButtonClickListener(this);
        rvJadwalSholat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJadwalSholat.setAdapter(jadwalSholatAdapter);
    }

    private void setupFilterToggle() {
        // Set state awal (misalnya, expanded)
        updateFilterVisibility();

        btnToggleFilter.setOnClickListener(v -> {
            isFilterExpanded = !isFilterExpanded;
            updateFilterVisibility();
        });
    }

    private void updateFilterVisibility() {
        if (isFilterExpanded) {
            layoutFilterContent.setVisibility(View.VISIBLE);
            btnToggleFilter.setImageResource(R.drawable.ic_arrow_up); // Ganti dengan ikon panah ke atas
        } else {
            layoutFilterContent.setVisibility(View.GONE);
            btnToggleFilter.setImageResource(R.drawable.ic_arrow_down); // Ganti dengan ikon panah ke bawah
        }
    }


    private void setupSpinners() {
        provinsiAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        provinsiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvinsi.setAdapter(provinsiAdapter);
        spinnerProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RegionItem selected = (RegionItem) parent.getItemAtPosition(position);
                if (selected != null && !selected.equals(viewModel.selectedProvince.getValue())) {
                    Log.d("Spinner", "Provinsi selected by user: " + selected.getName());
                    viewModel.selectedProvince.setValue(selected);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        kotaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        kotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKota.setAdapter(kotaAdapter);
        spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RegionItem selected = (RegionItem) parent.getItemAtPosition(position);
                if (selected != null && !selected.equals(viewModel.selectedCity.getValue())) {
                    Log.d("Spinner", "Kota selected by user: " + selected.getName());
                    viewModel.selectedCity.setValue(selected);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        bulanAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        bulanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBulan.setAdapter(bulanAdapter);
        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonthValue = String.format(Locale.US, "%02d", position + 1);
                if (!selectedMonthValue.equals(viewModel.selectedBulanValue.getValue())) {
                    Log.d("Spinner", "Bulan selected by user: " + selectedMonthValue);
                    viewModel.selectedBulanValue.setValue(selectedMonthValue);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        tahunAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        tahunAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTahun.setAdapter(tahunAdapter);
        spinnerTahun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = parent.getItemAtPosition(position).toString();
                if (!selectedYear.equals(viewModel.selectedTahunValue.getValue())) {
                    Log.d("Spinner", "Tahun selected by user: " + selectedYear);
                    viewModel.selectedTahunValue.setValue(selectedYear);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void observeViewModel() {
        viewModel.getProvinces().observe(getViewLifecycleOwner(), provinces -> {
            if (provinces != null) {
                Log.d("Observer", "Provinces LiveData updated, size: " + provinces.size());
                Object previouslySelected = spinnerProvinsi.getSelectedItem();
                provinsiAdapter.clear();
                provinsiAdapter.addAll(provinces);

                RegionItem currentVmSelection = viewModel.selectedProvince.getValue();
                if (currentVmSelection != null) {
                    int pos = provinsiAdapter.getPosition(currentVmSelection);
                    if (pos >= 0 && spinnerProvinsi.getSelectedItemPosition() != pos) { // Cek jika sudah terpilih
                        spinnerProvinsi.setSelection(pos, false);
                    }
                } else if (previouslySelected instanceof RegionItem && provinces.contains(previouslySelected)) {
                    int pos = provinsiAdapter.getPosition((RegionItem)previouslySelected);
                    if (pos >= 0 && spinnerProvinsi.getSelectedItemPosition() != pos) spinnerProvinsi.setSelection(pos, false);
                } else if (!provinces.isEmpty() && spinnerProvinsi.getSelectedItemPosition() == -1) {
                    // Tidak melakukan auto-select di sini, biarkan ViewModel yang mengontrol state awal
                }
            }
        });

        viewModel.selectedProvince.observe(getViewLifecycleOwner(), selectedProvince -> {
            if (selectedProvince != null) {
                Log.d("Observer", "ViewModel selectedProvince changed: " + selectedProvince.getName());
                // Hanya fetch cities jika selectedProvince benar-benar berubah atau kota belum dimuat
                if (viewModel.getCities().getValue() == null || viewModel.getCities().getValue().isEmpty() ||
                        (viewModel.selectedCity.getValue() != null && !selectedProvince.getId().equals(viewModel.selectedCity.getValue().getId().substring(0,2))) ) {
                    viewModel.fetchCitiesForProvince(selectedProvince.getId());
                }
            } else {
                kotaAdapter.clear();
                jadwalSholatAdapter.setWaktuSholatItemList(new ArrayList<>());
            }
        });

        viewModel.getCities().observe(getViewLifecycleOwner(), cities -> {
            if (cities != null) {
                Log.d("Observer", "Cities LiveData updated, size: " + cities.size());
                Object previouslySelected = spinnerKota.getSelectedItem();
                kotaAdapter.clear();
                kotaAdapter.addAll(cities);

                RegionItem currentVmSelection = viewModel.selectedCity.getValue();
                if (currentVmSelection != null) {
                    int pos = kotaAdapter.getPosition(currentVmSelection);
                    if (pos >= 0 && spinnerKota.getSelectedItemPosition() != pos) {
                        spinnerKota.setSelection(pos, false);
                    }
                } else if (previouslySelected instanceof RegionItem && cities.contains(previouslySelected)) {
                    int pos = kotaAdapter.getPosition((RegionItem)previouslySelected);
                    if (pos >= 0 && spinnerKota.getSelectedItemPosition() != pos) spinnerKota.setSelection(pos, false);
                } else if (!cities.isEmpty() && spinnerKota.getSelectedItemPosition() == -1) {
                    // Tidak melakukan auto-select
                }
            }
        });

        viewModel.selectedCity.observe(getViewLifecycleOwner(), selectedCity -> {
            if (selectedCity != null) {
                Log.d("Observer", "ViewModel selectedCity changed: " + selectedCity.getName());
                // Hanya fetch city ID jika selectedCity benar-benar berubah atau ID belum ada
                if (viewModel.getSelectedWaktuSholatApiCityId().getValue() == null ||
                        (viewModel.selectedCity.getValue()!=null && !selectedCity.getName().equalsIgnoreCase(viewModel.selectedCity.getValue().getName())) // Asumsi nama unik untuk fetch ID
                ) {
                    viewModel.fetchPrayerTimeApiCityId(selectedCity.getName());
                }
            } else {
                jadwalSholatAdapter.setWaktuSholatItemList(new ArrayList<>());
            }
        });

        viewModel.getMonths().observe(getViewLifecycleOwner(), months -> {
            if (months != null) {
                Log.d("Observer", "Months LiveData updated");
                Object previouslySelected = spinnerBulan.getSelectedItem();
                bulanAdapter.clear();
                bulanAdapter.addAll(months);
                String currentMonthValue = viewModel.selectedBulanValue.getValue();
                if (currentMonthValue != null) {
                    int monthIndex = Integer.parseInt(currentMonthValue) - 1;
                    if (monthIndex >= 0 && monthIndex < bulanAdapter.getCount() && spinnerBulan.getSelectedItemPosition() != monthIndex) {
                        spinnerBulan.setSelection(monthIndex, false);
                    }
                } else if (previouslySelected != null){
                    int pos = bulanAdapter.getPosition(previouslySelected.toString());
                    if (pos >=0 && spinnerBulan.getSelectedItemPosition() != pos) spinnerBulan.setSelection(pos, false);
                }
            }
        });

        viewModel.getYears().observe(getViewLifecycleOwner(), years -> {
            if (years != null) {
                Log.d("Observer", "Years LiveData updated");
                Object previouslySelected = spinnerTahun.getSelectedItem();
                tahunAdapter.clear();
                tahunAdapter.addAll(years);
                String currentYearValue = viewModel.selectedTahunValue.getValue();
                if (currentYearValue != null) {
                    int yearPos = tahunAdapter.getPosition(currentYearValue);
                    if (yearPos >= 0 && spinnerTahun.getSelectedItemPosition() != yearPos) {
                        spinnerTahun.setSelection(yearPos, false);
                    }
                } else if (previouslySelected != null){
                    int pos = tahunAdapter.getPosition(previouslySelected.toString());
                    if (pos >=0 && spinnerTahun.getSelectedItemPosition() != pos) spinnerTahun.setSelection(pos, false);
                }
            }
        });

        viewModel.getSelectedWaktuSholatApiCityId().observe(getViewLifecycleOwner(), cityId -> {
            Log.d("Observer", "ViewModel selectedWaktuSholatApiCityId changed: " + cityId);
            if (cityId != null && viewModel.selectedBulanValue.getValue() != null && viewModel.selectedTahunValue.getValue() != null) {
                viewModel.fetchPrayerTimes();
            } else if (cityId == null && viewModel.selectedCity.getValue() != null) { // Jika cityId jadi null setelah ada kota terpilih
                jadwalSholatAdapter.setWaktuSholatItemList(new ArrayList<>());
            }
        });

        // Observer terpisah untuk bulan dan tahun untuk memicu fetchPrayerTimes jika cityId sudah ada
        viewModel.selectedBulanValue.observe(getViewLifecycleOwner(), month -> {
            Log.d("Observer", "ViewModel selectedBulanValue changed: " + month);
            if (month != null && viewModel.getSelectedWaktuSholatApiCityId().getValue() != null && viewModel.selectedTahunValue.getValue() != null) {
                viewModel.fetchPrayerTimes();
            }
        });
        viewModel.selectedTahunValue.observe(getViewLifecycleOwner(), year -> {
            Log.d("Observer", "ViewModel selectedTahunValue changed: " + year);
            if (year != null && viewModel.getSelectedWaktuSholatApiCityId().getValue() != null && viewModel.selectedBulanValue.getValue() != null) {
                viewModel.fetchPrayerTimes();
            }
        });

        viewModel.getPrayerSchedule().observe(getViewLifecycleOwner(), waktuSholatItems -> {
            Log.d("Observer", "PrayerSchedule LiveData updated, size: " + (waktuSholatItems != null ? waktuSholatItems.size() : 0));
            jadwalSholatAdapter.setWaktuSholatItemList(waktuSholatItems);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                Toast.makeText(getContext(), "Memuat...", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                viewModel.clearErrorMessage();
            }
        });
    }

    @Override
    public void onAktifButtonClick(WaktuSholatItem item, int position) {
        Toast.makeText(getContext(), "Tombol Aktif diklik untuk: " + item.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNonaktifButtonClick(WaktuSholatItem item, int position) {
        Toast.makeText(getContext(), "Tombol Nonaktif diklik untuk: " + item.getName(), Toast.LENGTH_SHORT).show();
    }
}