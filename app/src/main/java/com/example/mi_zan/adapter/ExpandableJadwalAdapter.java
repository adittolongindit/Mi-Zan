package com.example.mi_zan.adapter; // Sesuaikan package

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_zan.R;
import com.example.mi_zan.model.DayGroup;
import com.example.mi_zan.model.PrayerScheduleDisplayItem;
import com.example.mi_zan.model.PrayerTimeDisplayWrapper;
import com.example.mi_zan.model.SinglePrayerTime;
import com.example.mi_zan.model.WeekGroup;

import java.util.ArrayList;
import java.util.List;

public class ExpandableJadwalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ExpandableJadwalAdapter";
    private Context context;
    private List<PrayerScheduleDisplayItem> displayItems;

    public ExpandableJadwalAdapter(Context context, List<PrayerScheduleDisplayItem> displayItems) {
        this.context = context;
        this.displayItems = displayItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= 0 && position < displayItems.size()) {
            return displayItems.get(position).getItemType();
        }
        return -1; // Tipe tidak valid jika posisi di luar batas
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_WEEK) {
            View view = inflater.inflate(R.layout.list_item_week_group, parent, false);
            return new WeekViewHolder(view);
        } else if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_DAY) {
            View view = inflater.inflate(R.layout.list_item_day_group, parent, false);
            return new DayViewHolder(view);
        } else if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_PRAYER) {
            View view = inflater.inflate(R.layout.list_item_jadwal, parent, false);
            return new PrayerTimeViewHolder(view);
        }
        // Fallback jika viewType tidak dikenal (seharusnya tidak terjadi)
        Log.e(TAG, "onCreateViewHolder: Unknown view type - " + viewType);
        View emptyView = new View(parent.getContext());
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(0,0));
        return new RecyclerView.ViewHolder(emptyView) {}; // ViewHolder kosong
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 'position' di sini adalah posisi layout, bukan adapter position.
        // Untuk interaksi, selalu gunakan holder.getAdapterPosition().

        int viewType = holder.getItemViewType(); // Dapatkan tipe view dari holder
        PrayerScheduleDisplayItem currentItem = displayItems.get(position); // Ambil item berdasarkan posisi yang diberikan

        if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_WEEK) {
            WeekGroup weekGroup = (WeekGroup) currentItem;
            WeekViewHolder weekViewHolder = (WeekViewHolder) holder;
            weekViewHolder.tvWeekLabel.setText(weekGroup.getWeekLabel());
            weekViewHolder.ivWeekExpandIcon.setImageResource(
                    weekGroup.isExpanded() ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);

            weekViewHolder.itemView.setOnClickListener(v -> {
                int adapterPos = weekViewHolder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    WeekGroup clickedWeekGroup = (WeekGroup) displayItems.get(adapterPos); // Ambil item terbaru dari list
                    if (clickedWeekGroup.isExpanded()) {
                        collapseWeek(adapterPos, clickedWeekGroup);
                    } else {
                        expandWeek(adapterPos, clickedWeekGroup);
                    }
                }
            });

        } else if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_DAY) {
            DayGroup dayGroup = (DayGroup) currentItem;
            DayViewHolder dayViewHolder = (DayViewHolder) holder;
            dayViewHolder.tvDayLabel.setText(dayGroup.getDayLabel());
            dayViewHolder.ivDayExpandIcon.setImageResource(
                    dayGroup.isExpanded() ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);

            dayViewHolder.itemView.setOnClickListener(v -> {
                int adapterPos = dayViewHolder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    DayGroup clickedDayGroup = (DayGroup) displayItems.get(adapterPos); // Ambil item terbaru
                    if (clickedDayGroup.isExpanded()) {
                        collapseDay(adapterPos, clickedDayGroup);
                    } else {
                        expandDay(adapterPos, clickedDayGroup);
                    }
                }
            });

        } else if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_PRAYER) {
            PrayerTimeDisplayWrapper prayerWrapper = (PrayerTimeDisplayWrapper) currentItem;
            SinglePrayerTime prayerTime = prayerWrapper.getSinglePrayerTime();
            PrayerTimeViewHolder prayerViewHolder = (PrayerTimeViewHolder) holder;

            prayerViewHolder.tvJudulJadwal.setText(prayerTime.getPrayerName());
            prayerViewHolder.tvTanggalItemJadwal.setText(prayerTime.getDateDisplay());
            prayerViewHolder.tvWaktuJadwal.setText(prayerTime.getPrayerTime());

            // Logika tombol aktif/nonaktif
            if (prayerTime.isActive()) {
                prayerViewHolder.btnAktif.setEnabled(false);
                prayerViewHolder.btnAktif.setAlpha(0.5f);
                prayerViewHolder.btnNonaktif.setEnabled(true);
                prayerViewHolder.btnNonaktif.setAlpha(1.0f);
            } else {
                prayerViewHolder.btnAktif.setEnabled(true);
                prayerViewHolder.btnAktif.setAlpha(1.0f);
                prayerViewHolder.btnNonaktif.setEnabled(false);
                prayerViewHolder.btnNonaktif.setAlpha(0.5f);
            }

            prayerViewHolder.btnAktif.setOnClickListener(v -> {
                int adapterPos = prayerViewHolder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    PrayerTimeDisplayWrapper currentWrapper = (PrayerTimeDisplayWrapper) displayItems.get(adapterPos);
                    SinglePrayerTime currentPrayerTime = currentWrapper.getSinglePrayerTime();
                    if (!currentPrayerTime.isActive()) {
                        currentPrayerTime.setActive(true);
                        notifyItemChanged(adapterPos);
                        Toast.makeText(context, currentPrayerTime.getPrayerName() + " (" + currentPrayerTime.getDateDisplay() + ") diaktifkan", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            prayerViewHolder.btnNonaktif.setOnClickListener(v -> {
                int adapterPos = prayerViewHolder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION) {
                    PrayerTimeDisplayWrapper currentWrapper = (PrayerTimeDisplayWrapper) displayItems.get(adapterPos);
                    SinglePrayerTime currentPrayerTime = currentWrapper.getSinglePrayerTime();
                    if (currentPrayerTime.isActive()) {
                        currentPrayerTime.setActive(false);
                        notifyItemChanged(adapterPos);
                        Toast.makeText(context, currentPrayerTime.getPrayerName() + " (" + currentPrayerTime.getDateDisplay() + ") dinonaktifkan", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void expandWeek(int position, WeekGroup weekGroup) {
        if (weekGroup.isExpanded() || weekGroup.getDayGroups() == null || weekGroup.getDayGroups().isEmpty()) {
            Log.d(TAG, "expandWeek: Already expanded or no children for week at position " + position);
            return;
        }
        weekGroup.setExpanded(true);

        int childStartPosition = position + 1;
        List<DayGroup> children = weekGroup.getDayGroups();

        if (childStartPosition <= displayItems.size()) { // Periksa batas sebelum addAll
            displayItems.addAll(childStartPosition, children);
            notifyItemRangeInserted(childStartPosition, children.size());
        } else {
            // Ini seharusnya tidak terjadi jika 'position' valid dan list konsisten
            Log.e(TAG, "expandWeek: childStartPosition (" + childStartPosition +
                    ") is out of bounds for displayItems size (" + displayItems.size() + "). Position: " + position);
            // Sebagai fallback yang kurang ideal, tambahkan ke akhir (mungkin merusak urutan visual)
            // displayItems.addAll(children);
            // notifyItemRangeInserted(displayItems.size() - children.size(), children.size());
            // Atau, lebih baik, log error dan jangan lakukan apa-apa untuk mencegah state yang lebih buruk
            weekGroup.setExpanded(false); // Kembalikan state jika gagal expand
            return;
        }
        notifyItemChanged(position); // Update icon expand dari week item
    }

    private void collapseWeek(int position, WeekGroup weekGroup) {
        if (!weekGroup.isExpanded()) {
            Log.d(TAG, "collapseWeek: Already collapsed for week at position " + position);
            return;
        }
        weekGroup.setExpanded(false);

        int itemsToRemoveCount = 0;
        // Hitung semua item anak yang akan dihapus (DayGroup dan PrayerTime-nya jika expanded)
        for (DayGroup day : weekGroup.getDayGroups()) {
            itemsToRemoveCount++; // Item DayGroup itu sendiri
            if (day.isExpanded()) {
                itemsToRemoveCount += day.getPrayerTimes().size();
                day.setExpanded(false); // Tutup juga DayGroup anaknya
            }
        }

        if (itemsToRemoveCount > 0) {
            int startRemoveIndex = position + 1;
            // Pastikan kita tidak mencoba menghapus di luar batas atau lebih banyak dari yang ada
            if (startRemoveIndex < displayItems.size() && (startRemoveIndex + itemsToRemoveCount) <= displayItems.size()) {
                for (int i = 0; i < itemsToRemoveCount; i++) {
                    // Selalu hapus dari startRemoveIndex karena list bergeser
                    if (startRemoveIndex < displayItems.size()){ // Double check sebelum remove
                        displayItems.remove(startRemoveIndex);
                    } else {
                        Log.e(TAG, "collapseWeek: Attempted to remove from out of bounds index during loop.");
                        break; // Keluar loop jika terjadi kesalahan batas
                    }
                }
                notifyItemRangeRemoved(startRemoveIndex, itemsToRemoveCount);
            } else {
                Log.e(TAG, "collapseWeek: Calculated itemsToRemoveCount (" + itemsToRemoveCount +
                        ") or startRemoveIndex (" + startRemoveIndex +
                        ") is problematic for displayItems size (" + displayItems.size() + "). Position: " + position);
                // Jika terjadi ketidaksesuaian, mungkin list tidak konsisten.
                // Sebagai fallback, bisa panggil notifyDataSetChanged() setelah membersihkan dan membangun ulang
                // displayItems berdasarkan state WeekGroup yang baru. Namun, ini akan menghilangkan animasi.
            }
        }
        notifyItemChanged(position); // Update icon expand dari week item
    }


    private void expandDay(int position, DayGroup dayGroup) {
        if (dayGroup.isExpanded() || dayGroup.getPrayerTimes() == null || dayGroup.getPrayerTimes().isEmpty()) {
            Log.d(TAG, "expandDay: Already expanded or no prayer times for day at position " + position);
            return;
        }
        dayGroup.setExpanded(true);

        int childStartPosition = position + 1;
        List<PrayerTimeDisplayWrapper> prayerWrappers = new ArrayList<>();
        for (SinglePrayerTime spt : dayGroup.getPrayerTimes()) {
            prayerWrappers.add(new PrayerTimeDisplayWrapper(spt));
        }

        if (childStartPosition <= displayItems.size()) { // Periksa batas
            displayItems.addAll(childStartPosition, prayerWrappers);
            notifyItemRangeInserted(childStartPosition, prayerWrappers.size());
        } else {
            Log.e(TAG, "expandDay: childStartPosition (" + childStartPosition +
                    ") is out of bounds for displayItems size (" + displayItems.size() + "). Position: " + position);
            dayGroup.setExpanded(false); // Kembalikan state
            return;
        }
        notifyItemChanged(position);
    }

    private void collapseDay(int position, DayGroup dayGroup) {
        if (!dayGroup.isExpanded() || dayGroup.getPrayerTimes() == null || dayGroup.getPrayerTimes().isEmpty()) {
            Log.d(TAG, "collapseDay: Already collapsed or no prayer times for day at position " + position);
            return;
        }
        dayGroup.setExpanded(false);

        int prayerItemCount = dayGroup.getPrayerTimes().size();

        if (prayerItemCount > 0) {
            int startRemoveIndex = position + 1;
            if (startRemoveIndex < displayItems.size() && (startRemoveIndex + prayerItemCount) <= displayItems.size()) {
                for (int i = 0; i < prayerItemCount; i++) {
                    if (startRemoveIndex < displayItems.size()){
                        displayItems.remove(startRemoveIndex);
                    } else {
                        Log.e(TAG, "collapseDay: Attempted to remove from out of bounds index during loop.");
                        break;
                    }
                }
                notifyItemRangeRemoved(startRemoveIndex, prayerItemCount);
            } else {
                Log.e(TAG, "collapseDay: Calculated prayerItemCount (" + prayerItemCount +
                        ") or startRemoveIndex (" + startRemoveIndex +
                        ") is problematic for displayItems size (" + displayItems.size() + "). Position: " + position);
            }
        }
        notifyItemChanged(position);
    }


    @Override
    public int getItemCount() {
        return displayItems.size();
    }

    public void updateData(List<WeekGroup> newWeekGroupsFromFragment) {
        displayItems.clear();
        if (newWeekGroupsFromFragment != null) {
            // Saat data baru masuk, semua item minggu akan dalam keadaan collapsed (isExpanded=false dari constructor WeekGroup)
            // Hanya WeekGroup yang ditambahkan ke displayItems pada awalnya.
            for(WeekGroup wg : newWeekGroupsFromFragment){
                // Pastikan state expanded direset jika objek WeekGroup digunakan kembali (seharusnya tidak jika dibuat baru)
                wg.setExpanded(false);
                // Jika DayGroup di dalamnya juga punya state expanded, reset juga.
                for(DayGroup dg : wg.getDayGroups()){
                    dg.setExpanded(false);
                }
                displayItems.add(wg);
            }
        }
        notifyDataSetChanged(); // Setelah data di-reset, gunakan notifyDataSetChanged
    }

    // ViewHolder untuk WeekGroup
    static class WeekViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeekLabel;
        ImageView ivWeekExpandIcon;
        WeekViewHolder(View itemView) {
            super(itemView);
            tvWeekLabel = itemView.findViewById(R.id.tv_week_label);
            ivWeekExpandIcon = itemView.findViewById(R.id.iv_week_expand_icon);
        }
    }

    // ViewHolder untuk DayGroup
    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayLabel;
        ImageView ivDayExpandIcon;
        DayViewHolder(View itemView) {
            super(itemView);
            tvDayLabel = itemView.findViewById(R.id.tv_day_label);
            ivDayExpandIcon = itemView.findViewById(R.id.iv_day_expand_icon);
        }
    }

    // ViewHolder untuk SinglePrayerTime (PrayerTimeViewHolder)
    static class PrayerTimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulJadwal, tvWaktuJadwal, tvTanggalItemJadwal;
        Button btnAktif, btnNonaktif;
        PrayerTimeViewHolder(View itemView) {
            super(itemView);
            tvJudulJadwal = itemView.findViewById(R.id.judul_jadwal);
            tvTanggalItemJadwal = itemView.findViewById(R.id.tv_tanggal_item_jadwal); // Pastikan ID ini ada di list_item_jadwal.xml
            tvWaktuJadwal = itemView.findViewById(R.id.waktu_jadwal);
            btnAktif = itemView.findViewById(R.id.btn_aktif);
            btnNonaktif = itemView.findViewById(R.id.btn_nonaktif);
        }
    }
}