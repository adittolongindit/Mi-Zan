package com.example.mi_zan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        MaterialButton btnGantiTema = view.findViewById(R.id.btn_ganti_tema);

        // Set teks awal sesuai tema saat ini
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            btnGantiTema.setText("Dark");
        } else {
            btnGantiTema.setText("Light");
        }

        btnGantiTema.setOnClickListener(v -> {
            int currentMode = AppCompatDelegate.getDefaultNightMode();

            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES || currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            requireActivity().recreate();
        });


        return view;
    }
}
