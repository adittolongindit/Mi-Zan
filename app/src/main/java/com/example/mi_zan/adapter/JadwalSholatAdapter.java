package com.example.mi_zan.adapter; // Sesuaikan dengan package Anda

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_zan.R;
import com.example.mi_zan.model.SinglePrayerTime; // Import model baru

import java.util.List;

public class JadwalSholatAdapter extends RecyclerView.Adapter<JadwalSholatAdapter.ViewHolder> {

    private List<SinglePrayerTime> singlePrayerTimeList; // Menggunakan model baru
    private Context context;

    public JadwalSholatAdapter(Context context, List<SinglePrayerTime> singlePrayerTimeList) {
        this.context = context;
        this.singlePrayerTimeList = singlePrayerTimeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jadwal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SinglePrayerTime item = singlePrayerTimeList.get(position);

        holder.tvJudulJadwal.setText(item.getPrayerName());
        holder.tvTanggalItemJadwal.setText(item.getDateDisplay()); // Set teks untuk tanggal
        holder.tvWaktuJadwal.setText(item.getPrayerTime());

        // Update tampilan tombol berdasarkan status isActive
        if (item.isActive()) {
            holder.btnAktif.setEnabled(false);
            holder.btnAktif.setAlpha(0.5f); // Tampak redup jika sudah aktif
            holder.btnNonaktif.setEnabled(true);
            holder.btnNonaktif.setAlpha(1.0f);
        } else {
            holder.btnAktif.setEnabled(true);
            holder.btnAktif.setAlpha(1.0f);
            holder.btnNonaktif.setEnabled(false);
            holder.btnNonaktif.setAlpha(0.5f); // Tampak redup jika sudah nonaktif
        }

        holder.btnAktif.setOnClickListener(v -> {
            if (!item.isActive()) { // Hanya aktifkan jika belum aktif
                item.setActive(true);
                notifyItemChanged(holder.getAdapterPosition()); // Refresh item ini
                // Di sini Anda bisa menambahkan logika untuk menyimpan status (misal ke SharedPreferences)
                Toast.makeText(context, item.getPrayerName() + " (" + item.getDateDisplay() + ") diaktifkan", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnNonaktif.setOnClickListener(v -> {
            if (item.isActive()) { // Hanya nonaktifkan jika masih aktif
                item.setActive(false);
                notifyItemChanged(holder.getAdapterPosition()); // Refresh item ini
                // Di sini Anda bisa menambahkan logika untuk menyimpan status
                Toast.makeText(context, item.getPrayerName() + " (" + item.getDateDisplay() + ") dinonaktifkan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return singlePrayerTimeList.size();
    }

    // Ganti nama method atau overload untuk data baru
    public void updateData(List<SinglePrayerTime> newSinglePrayerTimeList) {
        this.singlePrayerTimeList.clear();
        this.singlePrayerTimeList.addAll(newSinglePrayerTimeList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulJadwal, tvWaktuJadwal, tvTanggalItemJadwal; // Tambahkan tvTanggalItemJadwal
        Button btnAktif, btnNonaktif;

        ViewHolder(View itemView) {
            super(itemView);
            tvJudulJadwal = itemView.findViewById(R.id.judul_jadwal);
            tvTanggalItemJadwal = itemView.findViewById(R.id.tv_tanggal_item_jadwal); // Inisialisasi TextView tanggal
            tvWaktuJadwal = itemView.findViewById(R.id.waktu_jadwal);
            btnAktif = itemView.findViewById(R.id.btn_aktif);
            btnNonaktif = itemView.findViewById(R.id.btn_nonaktif);
        }
    }
}