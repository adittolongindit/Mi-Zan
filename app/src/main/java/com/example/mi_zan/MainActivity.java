package com.example.mi_zan;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Inisialisasi BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment()); // Set HomeFragment sebagai fragment default
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            // Ganti switch dengan if-else
            if (item.getItemId() == R.id.item_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.item_maps) {
                fragment = new MapsFragment();
            } else if (item.getItemId() == R.id.item_profile) {
                fragment = new ProfileFragment();
            } else if (item.getItemId() == R.id.item_camera) {
                fragment = new CameraFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true; // Mengindikasikan bahwa item telah dipilih
        });
    }

    // Fungsi untuk mengganti fragment dengan animasi
    private void loadFragment(Fragment fragment) {
        // Membuat transaksi fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Menambahkan animasi transisi kustom
        transaction.setCustomAnimations(
                R.anim.masuk_dari_kiri,
                R.anim.keluar_ke_kanan
        );

        // Ganti fragment
        transaction.replace(R.id.fragment_container, fragment); // Pastikan R.id.fragment_container adalah kontainer untuk menampilkan fragment
        transaction.addToBackStack(null); // Opsional, jika kamu ingin memungkinkan tombol back untuk kembali ke fragment sebelumnya
        transaction.commit();
    }
}

