package com.example.mi_zan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MasjidAPI masjidApi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_fragment, container, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://206.189.84.169:5700/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        masjidApi = retrofit.create(MasjidAPI.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getUserLocationAndDisplayMasjid();
    }

    private void getUserLocationAndDisplayMasjid() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng userLocation = new LatLng(latitude, longitude);

                        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12)); // Zoom level 12

                        String radius = "5000";
                        masjidApi.getMasjidNearby(String.valueOf(latitude), String.valueOf(longitude), radius)
                                .enqueue(new Callback<MasjidResponse>() {
                                    @Override
                                    public void onResponse(Call<MasjidResponse> call, Response<MasjidResponse> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            for (MasjidResponse.Masjid masjid : response.body().masjidList) {
                                                LatLng masjidLatLng = new LatLng(Double.parseDouble(masjid.location.lat), Double.parseDouble(masjid.location.lng));
                                                mMap.addMarker(new MarkerOptions()
                                                        .position(masjidLatLng)
                                                        .title(masjid.name)
                                                        .snippet(masjid.address));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MasjidResponse> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                    }
                });
    }
}
