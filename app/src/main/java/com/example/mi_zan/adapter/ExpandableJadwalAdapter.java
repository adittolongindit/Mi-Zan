package com.example.mi_zan.adapter;

import android.content.Context;
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

    private Context context;
    private List<PrayerScheduleDisplayItem> displayItems; // List utama yang ditampilkan

    public ExpandableJadwalAdapter(Context context, List<PrayerScheduleDisplayItem> displayItems) {
        this.context = context;
        this.displayItems = displayItems; // Awalnya hanya berisi WeekGroup
    }

    @Override
    public int getItemViewType(int position) {
        return displayItems.get(position).getItemType();
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
            View view = inflater.inflate(R.layout.list_item_jadwal, parent, false); // Layout item jadwal yang sudah ada
            return new PrayerTimeViewHolder(view);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        PrayerScheduleDisplayItem currentItem = displayItems.get(position);

        if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_WEEK) {
            WeekGroup weekGroup = (WeekGroup) currentItem;
            WeekViewHolder weekViewHolder = (WeekViewHolder) holder;
            weekViewHolder.tvWeekLabel.setText(weekGroup.getWeekLabel());
            weekViewHolder.ivWeekExpandIcon.setImageResource(
                    weekGroup.isExpanded() ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down); // Ganti icon

            weekViewHolder.itemView.setOnClickListener(v -> {
                if (weekGroup.isExpanded()) {
                    collapseWeek(position, weekGroup);
                } else {
                    expandWeek(position, weekGroup);
                }
                weekGroup.setExpanded(!weekGroup.isExpanded());
            });

        } else if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_DAY) {
            DayGroup dayGroup = (DayGroup) currentItem;
            DayViewHolder dayViewHolder = (DayViewHolder) holder;
            dayViewHolder.tvDayLabel.setText(dayGroup.getDayLabel());
            dayViewHolder.ivDayExpandIcon.setImageResource(
                    dayGroup.isExpanded() ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down); // Ganti icon

            dayViewHolder.itemView.setOnClickListener(v -> {
                if (dayGroup.isExpanded()) {
                    collapseDay(position, dayGroup);
                } else {
                    expandDay(position, dayGroup);
                }
                dayGroup.setExpanded(!dayGroup.isExpanded());
            });

        } else if (viewType == PrayerScheduleDisplayItem.VIEW_TYPE_PRAYER) {
            PrayerTimeDisplayWrapper prayerWrapper = (PrayerTimeDisplayWrapper) currentItem;
            SinglePrayerTime prayerTime = prayerWrapper.getSinglePrayerTime();
            PrayerTimeViewHolder prayerViewHolder = (PrayerTimeViewHolder) holder;

            prayerViewHolder.tvJudulJadwal.setText(prayerTime.getPrayerName());
            prayerViewHolder.tvTanggalItemJadwal.setText(prayerTime.getDateDisplay());
            prayerViewHolder.tvWaktuJadwal.setText(prayerTime.getPrayerTime());

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
                if (!prayerTime.isActive()) {
                    prayerTime.setActive(true);
                    notifyItemChanged(holder.getAdapterPosition());
                    Toast.makeText(context, prayerTime.getPrayerName() + " (" + prayerTime.getDateDisplay() + ") diaktifkan", Toast.LENGTH_SHORT).show();
                }
            });

            prayerViewHolder.btnNonaktif.setOnClickListener(v -> {
                if (prayerTime.isActive()) {
                    prayerTime.setActive(false);
                    notifyItemChanged(holder.getAdapterPosition());
                    Toast.makeText(context, prayerTime.getPrayerName() + " (" + prayerTime.getDateDisplay() + ") dinonaktifkan", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void expandWeek(int position, WeekGroup weekGroup) {
        if (weekGroup.getDayGroups() != null && !weekGroup.getDayGroups().isEmpty()) {
            int childStartPosition = position + 1;
            displayItems.addAll(childStartPosition, weekGroup.getDayGroups());
            notifyItemRangeInserted(childStartPosition, weekGroup.getDayGroups().size());
            notifyItemChanged(position); // Untuk update icon expand
        }
    }

    private void collapseWeek(int position, WeekGroup weekGroup) {
        List<PrayerScheduleDisplayItem> itemsToRemove = new ArrayList<>();
        // Hapus semua anak dari week ini (DayGroup dan PrayerTimeDisplayWrapper jika ada yang terbuka)
        int i = position + 1;
        while (i < displayItems.size()) {
            PrayerScheduleDisplayItem item = displayItems.get(i);
            if (item.getItemType() == PrayerScheduleDisplayItem.VIEW_TYPE_WEEK) { // Sudah sampai week berikutnya
                break;
            }
            itemsToRemove.add(item);
            // Jika DayGroup yang dihapus juga expanded, set expanded jadi false
            if(item.getItemType() == PrayerScheduleDisplayItem.VIEW_TYPE_DAY){
                ((DayGroup)item).setExpanded(false);
            }
            i++;
        }

        if (!itemsToRemove.isEmpty()) {
            displayItems.removeAll(itemsToRemove);
            notifyItemRangeRemoved(position + 1, itemsToRemove.size());
        }
        notifyItemChanged(position); // Untuk update icon expand
    }


    private void expandDay(int position, DayGroup dayGroup) {
        if (dayGroup.getPrayerTimes() != null && !dayGroup.getPrayerTimes().isEmpty()) {
            int childStartPosition = position + 1;
            List<PrayerTimeDisplayWrapper> prayerWrappers = new ArrayList<>();
            for(SinglePrayerTime spt : dayGroup.getPrayerTimes()){
                prayerWrappers.add(new PrayerTimeDisplayWrapper(spt));
            }
            displayItems.addAll(childStartPosition, prayerWrappers);
            notifyItemRangeInserted(childStartPosition, prayerWrappers.size());
            notifyItemChanged(position); // Untuk update icon expand
        }
    }

    private void collapseDay(int position, DayGroup dayGroup) {
        if (dayGroup.getPrayerTimes() != null && !dayGroup.getPrayerTimes().isEmpty()) {
            int childCount = 0;
            // Hitung berapa banyak prayer times (VIEW_TYPE_PRAYER) di bawah hari ini
            for (int i = 0; i < dayGroup.getPrayerTimes().size(); i++) {
                if ((position + 1 + i) < displayItems.size() &&
                        displayItems.get(position + 1 + i).getItemType() == PrayerScheduleDisplayItem.VIEW_TYPE_PRAYER) {
                    childCount++;
                } else {
                    break; // Berhenti jika bukan prayer item atau keluar batas
                }
            }

            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    displayItems.remove(position + 1); // Hapus satu per satu dari posisi setelah parent
                }
                notifyItemRangeRemoved(position + 1, childCount);
            }
        }
        notifyItemChanged(position); // Untuk update icon expand
    }


    @Override
    public int getItemCount() {
        return displayItems.size();
    }

    public void updateData(List<WeekGroup> newWeekGroups) {
        displayItems.clear();
        if (newWeekGroups != null) {
            // Awalnya hanya tampilkan WeekGroup
            displayItems.addAll(newWeekGroups);
        }
        notifyDataSetChanged();
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

    // ViewHolder untuk SinglePrayerTime (menggunakan layout list_item_jadwal)
    // Ini sama dengan ViewHolder di JadwalSholatAdapter sebelumnya
    static class PrayerTimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulJadwal, tvWaktuJadwal, tvTanggalItemJadwal;
        Button btnAktif, btnNonaktif;
        PrayerTimeViewHolder(View itemView) {
            super(itemView);
            tvJudulJadwal = itemView.findViewById(R.id.judul_jadwal);
            tvTanggalItemJadwal = itemView.findViewById(R.id.tv_tanggal_item_jadwal);
            tvWaktuJadwal = itemView.findViewById(R.id.waktu_jadwal);
            btnAktif = itemView.findViewById(R.id.btn_aktif);
            btnNonaktif = itemView.findViewById(R.id.btn_nonaktif);
        }
    }
}