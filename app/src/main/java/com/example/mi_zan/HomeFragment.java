package com.example.mi_zan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    private Button cameraButton;
    private TextView currentTimeTextView;
    private ViewPager2 prayerCarousel;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private final String[] waktuSholat = {
            "Shubuh 05:00 AM",
            "Dzuhur 12:00 PM",
            "Ashar 03:30 PM",
            "Maghrib 06:45 PM",
            "Isya 08:15 PM"
    };

    private int currentPage = 0;
    private final Handler handler = new Handler();
    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (prayerCarousel != null && waktuSholat.length > 0) {
                currentPage = (currentPage + 1) % waktuSholat.length;
                prayerCarousel.setCurrentItem(currentPage, true);
                handler.postDelayed(this, 10000);
            }
        }
    };

    private final Handler timeHandler = new Handler();
    private final Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentTimeTextView != null) {
                currentTimeTextView.setText(getCurrentTime());
                timeHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        timeHandler.post(updateTimeRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        timeHandler.removeCallbacks(updateTimeRunnable);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        Button profilButton = view.findViewById(R.id.profil_button);

        currentTimeTextView = view.findViewById(R.id.current_time);

        profilButton.setOnClickListener(v -> {
            Fragment profilFragment = new ProfileFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, profilFragment)
                    .addToBackStack(null)
                    .commit();
        });

//        view.findViewById(R.id.doa_button).setOnClickListener(v -> navigateFullScreen(new DoaFragment()));
        view.findViewById(R.id.jadwal_sholat_button).setOnClickListener(v -> navigateFullScreen(new JadwalSholatFragment()));
/*        view.findViewById(R.id.quotes_button).setOnClickListener(v -> navigateFullScreen(new QuotesFragment()));
        view.findViewById(R.id.masjid_button).setOnClickListener(v -> navigateFullScreen(new MasjidFragment()));
        view.findViewById(R.id.jurnal_button).setOnClickListener(v -> navigateFullScreen(new JurnalFragment()));
        view.findViewById(R.id.jurnal_butto).setOnClickListener(v -> navigateFullScreen(new BingungFragment()));*/
        Button openCameraBtn = view.findViewById(R.id.camera_button);
        openCameraBtn.setOnClickListener(v -> checkCameraPermission());

        prayerCarousel = view.findViewById(R.id.next_prayer_carousel);
        NextPrayAdapter adapter = new NextPrayAdapter(waktuSholat);
        prayerCarousel.setAdapter(adapter);

        handler.postDelayed(autoScrollRunnable, 4000);
        return view;
    }

    private void navigateFullScreen(Fragment fragment) {
        if (requireActivity() instanceof MainActivity) {
            ((MainActivity) requireActivity()).hideBottomNavigation();
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(autoScrollRunnable);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date());
    }

    public void updateTime(String currentTime) {
        currentTimeTextView.setText(currentTime);
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    public void openAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                openAppSettings(requireContext());
                Toast.makeText(getContext(), "Izin Kamera Diperlukan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}