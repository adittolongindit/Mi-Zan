package com.example.mi_zan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import Button
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mi_zan.R;
import com.example.mi_zan.model.WaktuSholatItem;
import java.util.ArrayList;
import java.util.List;

public class JadwalSholatAdapter extends RecyclerView.Adapter<JadwalSholatAdapter.ViewHolder> {
    private List<WaktuSholatItem> waktuSholatItemList = new ArrayList<>();
    private OnItemButtonClickListener listener; // Listener untuk klik tombol

    // Interface untuk menangani klik tombol
    public interface OnItemButtonClickListener {
        void onAktifButtonClick(WaktuSholatItem item, int position);
        void onNonaktifButtonClick(WaktuSholatItem item, int position);
    }

    public void setOnItemButtonClickListener(OnItemButtonClickListener listener) {
        this.listener = listener;
    }

    public void setWaktuSholatItemList(List<WaktuSholatItem> waktuSholatItemList) {
        this.waktuSholatItemList.clear();
        if (waktuSholatItemList != null) {
            this.waktuSholatItemList.addAll(waktuSholatItemList);
        }
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jadwal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaktuSholatItem item = waktuSholatItemList.get(position);
        holder.tvJudulJadwal.setText(item.getName());
        holder.tvWaktuJadwal.setText(item.getTime());

        // Setup listener untuk tombol jika listener di-set
        if (listener != null) {
            holder.btnAktif.setOnClickListener(v -> listener.onAktifButtonClick(item, position));
            holder.btnNonaktif.setOnClickListener(v -> listener.onNonaktifButtonClick(item, position));
        }
    }

    @Override public int getItemCount() { return waktuSholatItemList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulJadwal;
        TextView tvWaktuJadwal;
        Button btnAktif; // Tambahkan Button
        Button btnNonaktif; // Tambahkan Button

        ViewHolder(View itemView) {
            super(itemView);
            tvJudulJadwal = itemView.findViewById(R.id.judul_jadwal);
            tvWaktuJadwal = itemView.findViewById(R.id.waktu_jadwal);
            btnAktif = itemView.findViewById(R.id.btn_aktif); // Hubungkan dengan ID di XML
            btnNonaktif = itemView.findViewById(R.id.btn_nonaktif); // Hubungkan dengan ID di XML
        }
    }
}