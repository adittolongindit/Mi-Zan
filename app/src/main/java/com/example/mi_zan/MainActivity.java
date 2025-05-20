package com.example.mi_zan;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        final Animation fadeAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_out);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.item_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.item_maps) {
                fragment = new MapsFragment();
            } else if (item.getItemId() == R.id.item_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                bottomNavigationView.startAnimation(fadeAnim);
                loadFragment(fragment);
            }
            return true;
        });
        /*bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.item_home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.item_maps) {
                fragment = new MapsFragment();
            } else if (item.getItemId() == R.id.item_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });*/
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(
                R.anim.masuk_dari_kiri,
                R.anim.keluar_ke_kanan
        );

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /*private void loadFragment(Fragment fragment) {
        // Load the translate animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_navigation_translate);

        // Start animation
        bottomNavigationView.startAnimation(animation);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.bottom_navigation_translate,  // masuk dari kiri
                R.anim.bottom_navigation_translate   // keluar ke kanan
        );
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/
}

