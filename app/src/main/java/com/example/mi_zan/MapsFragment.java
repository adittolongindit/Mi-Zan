package com.example.mi_zan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log; // Tambahkan Log untuk debugging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull; // Tambahkan NonNull
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.mi_zan.model.Center;
import com.example.mi_zan.model.Circle;
import com.example.mi_zan.model.LocationRestriction;
import com.example.mi_zan.model.SearchNearbyRequestBody;
import com.example.mi_zan.model.SearchNearbyResponseBody;
import com.example.mi_zan.model.PlaceResult; // Import PlaceResult
import com.example.mi_zan.service.GooglePlacesApiService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker; // Import Marker
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList; // Import ArrayList
import java.util.Arrays; // Import Arrays
import java.util.List; // Import List
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapsFragment"; // Untuk logging
    private GoogleMap mMap;
    private GooglePlacesApiService placesApiService; // Ganti dengan interface baru
    private FusedLocationProviderClient fusedLocationClient; // Pindahkan ke level class
    private List<Marker> mosqueMarkers = new ArrayList<>(); // Untuk menyimpan marker masjid
    private static final String GOOGLE_PLACES_API_KEY = "AIzaSyAiK0GW26eQyYfOjj_NFHKpJ5tuqaMO2zY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_fragment, container, false);

        if (getContext() != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://places.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        placesApiService = retrofit.create(GooglePlacesApiService.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        getUserLocationAndDisplayMasjid();
    }

    private void getUserLocationAndDisplayMasjid() {
        if (getContext() == null || getActivity() == null) {
            Log.e(TAG, "Context or Activity is null in getUserLocationAndDisplayMasjid");
            return;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng userLocation = new LatLng(latitude, longitude);

                        // Hapus marker user sebelumnya jika ada (jika method ini dipanggil ulang)
                        // mMap.clear(); // Ini akan menghapus semua marker, termasuk masjid. Handle dengan lebih baik jika perlu.

                        // Tambahkan marker untuk lokasi pengguna (atau gunakan fitur MyLocation bawaan)
                        // mMap.addMarker(new MarkerOptions().position(userLocation).title("Lokasi Saya"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14)); // Zoom level disesuaikan

                        fetchNearbyMosques(latitude, longitude);

                    } else {
                        Log.w(TAG, "Last location is null");
                    }
                })
                .addOnFailureListener(getActivity(), e -> {
                    Log.e(TAG, "Error getting location", e);
                });
    }

    private void fetchNearbyMosques(double lat, double lng) {
        if (GOOGLE_PLACES_API_KEY.equals("MASUKKAN_API_KEY_ANDA_DISINI")) {
            Log.e(TAG, "API Key belum dimasukkan. Silakan ganti GOOGLE_PLACES_API_KEY.");
            return;
        }

        Center center = new Center(lat, lng);
        // Radius dalam meter (5000m = 5km)
        Circle circle = new Circle(center, 5000.0);
        LocationRestriction restriction = new LocationRestriction(circle);

        SearchNearbyRequestBody requestBody = new SearchNearbyRequestBody(
                Arrays.asList("mosque"), // Tipe tempat: masjid
                20, // Jumlah hasil maksimal
                restriction
                // Jika ingin mengurutkan berdasarkan jarak terdekat:
                // tambahkan field rankPreference di SearchNearbyRequestBody
                // dan set requestBody.setRankPreference("DISTANCE");
                // Namun, "DISTANCE" rankPreference mengharuskan Anda TIDAK menggunakan radius
                // atau types (atau hanya satu type). Jadi untuk "mosque" lebih baik tanpa rankPreference
                // dan biarkan diurutkan berdasarkan relevansi/prominens dalam radius.
        );

        // Field mask menentukan field mana saja yang ingin Anda dapatkan dari API
        String fieldMask = "places.id,places.displayName,places.formattedAddress,places.location";

        placesApiService.searchNearbyPlaces(GOOGLE_PLACES_API_KEY, fieldMask, requestBody)
                .enqueue(new Callback<SearchNearbyResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<SearchNearbyResponseBody> call, @NonNull Response<SearchNearbyResponseBody> response) {
                        // Hapus marker masjid sebelumnya
                        for (Marker marker : mosqueMarkers) {
                            marker.remove();
                        }
                        mosqueMarkers.clear();

                        if (response.isSuccessful() && response.body() != null && response.body().getPlaces() != null) {
                            Log.d(TAG, "Masjid ditemukan: " + response.body().getPlaces().size());
                            for (PlaceResult masjid : response.body().getPlaces()) {
                                if (masjid.getLocation() != null && masjid.getDisplayName() != null) {
                                    LatLng masjidLatLng = new LatLng(masjid.getLocation().getLatitude(), masjid.getLocation().getLongitude());
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(masjidLatLng)
                                            .title(masjid.getDisplayName().getText())
                                            .snippet(masjid.getFormattedAddress())); // Snippet bisa alamat atau info lain
                                    if (marker != null) {
                                        mosqueMarkers.add(marker);
                                    }
                                }
                            }
                        } else {
                            Log.e(TAG, "Response tidak berhasil atau body null. Code: " + response.code());
                            if (response.errorBody() != null) {
                                try {
                                    Log.e(TAG, "Error body: " + response.errorBody().string());
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing error body", e);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SearchNearbyResponseBody> call, @NonNull Throwable t) {
                        Log.e(TAG, "Gagal mengambil data masjid", t);
                    }
                });
    }

    // Override onRequestPermissionsResult untuk menangani hasil permintaan izin
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
                getUserLocationAndDisplayMasjid(); // Coba lagi mendapatkan lokasi setelah izin diberikan
            } else {
                Log.w(TAG, "Izin lokasi ditolak");
                // Handle kasus izin ditolak, mungkin tampilkan pesan ke pengguna
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hapus semua marker saat view dihancurkan untuk menghindari memory leak jika fragment dibuat ulang
        for (Marker marker : mosqueMarkers) {
            marker.remove();
        }
        mosqueMarkers.clear();
        if (mMap != null) {
            // mMap.clear(); // Ini akan menghapus semua dari peta, termasuk layer MyLocation jika ada
        }
    }
}