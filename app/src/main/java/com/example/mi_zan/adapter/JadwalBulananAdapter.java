package com.example.mi_zan.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mi_zan.R;
import com.example.mi_zan.db.Alarm;
import com.example.mi_zan.db.AppDatabase;
import com.example.mi_zan.model.JadwalItem;
import com.example.mi_zan.service.PenjadwalanAlarm;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class JadwalBulananAdapter extends RecyclerView.Adapter<JadwalBulananAdapter.ViewHolder> {
    private List<JadwalItem> jadwalBulanan;
    private final Context context;

    public JadwalBulananAdapter(List<JadwalItem> jadwalBulanan, Context context) {
        this.jadwalBulanan = jadwalBulanan;
        this.context = context;
    }

    public void updateData(List<JadwalItem> newSchedule) {
        this.jadwalBulanan = newSchedule;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_jadwal_bulanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JadwalItem dailySchedule = jadwalBulanan.get(position);
        holder.tvTanggalHeader.setText(dailySchedule.getTanggal());
        holder.tvImsak.setText(dailySchedule.getImsak());
        holder.tvSubuh.setText(dailySchedule.getSubuh());
        holder.tvTerbit.setText(dailySchedule.getTerbit());
        holder.tvDhuha.setText(dailySchedule.getDhuha());
        holder.tvDzuhur.setText(dailySchedule.getDzuhur());
        holder.tvAshar.setText(dailySchedule.getAshar());
        holder.tvMaghrib.setText(dailySchedule.getMaghrib());
        holder.tvIsya.setText(dailySchedule.getIsya());

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date scheduleDate = sdf.parse(dailySchedule.getDate());
            Calendar todayCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            Date today = todayCal.getTime();
            if (scheduleDate != null && scheduleDate.before(today)) {
                holder.cardDaily.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            } else {
                holder.cardDaily.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setupAlarmIcon(holder.ivAlarmImsak, dailySchedule, "Imsak", dailySchedule.getImsak());
        setupAlarmIcon(holder.ivAlarmSubuh, dailySchedule, "Subuh", dailySchedule.getSubuh());
        setupAlarmIcon(holder.ivAlarmTerbit, dailySchedule, "Terbit", dailySchedule.getTerbit());
        setupAlarmIcon(holder.ivAlarmDhuha, dailySchedule, "Dhuha", dailySchedule.getDhuha());
        setupAlarmIcon(holder.ivAlarmDzuhur, dailySchedule, "Dzuhur", dailySchedule.getDzuhur());
        setupAlarmIcon(holder.ivAlarmAshar, dailySchedule, "Ashar", dailySchedule.getAshar());
        setupAlarmIcon(holder.ivAlarmMaghrib, dailySchedule, "Maghrib", dailySchedule.getMaghrib());
        setupAlarmIcon(holder.ivAlarmIsya, dailySchedule, "Isya", dailySchedule.getIsya());
    }

    private void setupAlarmIcon(ImageView alarmIcon, JadwalItem dailySchedule, String prayerName, String prayerTime) {
        String alarmId = dailySchedule.getDate() + "-" + prayerName;
        AsyncTask.execute(() -> {
            Alarm existingAlarm = AppDatabase.getDatabase(context).alarmDao().getAlarmById(alarmId);
            final boolean isCurrentlyEnabled = existingAlarm != null && existingAlarm.isEnabled();

            alarmIcon.post(() -> {
                updateAlarmIconUI(alarmIcon, isCurrentlyEnabled);
                alarmIcon.setOnClickListener(v -> toggleAlarm(alarmIcon, dailySchedule, prayerName, prayerTime, alarmId));
            });
        });
    }

    private void toggleAlarm(ImageView alarmIcon, JadwalItem dailySchedule, String prayerName, String prayerTime, String alarmId) {
        AsyncTask.execute(() -> {
            Alarm existingAlarm = AppDatabase.getDatabase(context).alarmDao().getAlarmById(alarmId);
            boolean newStatus = existingAlarm == null || !existingAlarm.isEnabled();

            Calendar calendar = Calendar.getInstance();
            try {
                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                dateSdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
                Date date = dateSdf.parse(dailySchedule.getDate() + " " + prayerTime);
                calendar.setTime(date);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            Alarm alarm = new Alarm(alarmId, prayerName, calendar.getTimeInMillis(), newStatus);
            AppDatabase.getDatabase(context).alarmDao().insertOrUpdate(alarm);

            alarmIcon.post(() -> {
                if (newStatus) {
                    PenjadwalanAlarm.onAlarm(context, alarm);
                } else {
                    PenjadwalanAlarm.offAlarm(context, alarm);
                }
                updateAlarmIconUI(alarmIcon, newStatus);
            });
        });
    }

    private void updateAlarmIconUI(ImageView alarmIcon, boolean isEnabled) {
        if (isEnabled) {
            alarmIcon.setImageResource(R.drawable.ic_alarm_on);
        } else {
            alarmIcon.setImageResource(R.drawable.ic_alarm_off);
        }
    }

    @Override
    public int getItemCount() {
        return jadwalBulanan.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardDaily;
        TextView tvTanggalHeader, tvImsak, tvSubuh, tvTerbit, tvDhuha, tvDzuhur, tvAshar, tvMaghrib, tvIsya;
        ImageView ivAlarmImsak, ivAlarmSubuh, ivAlarmTerbit, ivAlarmDhuha, ivAlarmDzuhur, ivAlarmAshar, ivAlarmMaghrib, ivAlarmIsya;

        ViewHolder(View itemView) {
            super(itemView);
            cardDaily = itemView.findViewById(R.id.card_daily);
            tvTanggalHeader = itemView.findViewById(R.id.tv_tanggal_header);

            // PERBAIKAN: Lengkapi semua inisialisasi TextView
            tvImsak = itemView.findViewById(R.id.tv_imsak_time);
            tvSubuh = itemView.findViewById(R.id.tv_subuh_time);
            tvTerbit = itemView.findViewById(R.id.tv_terbit_time);
            tvDhuha = itemView.findViewById(R.id.tv_dhuha_time);
            tvDzuhur = itemView.findViewById(R.id.tv_dzuhur_time);
            tvAshar = itemView.findViewById(R.id.tv_ashar_time);
            tvMaghrib = itemView.findViewById(R.id.tv_maghrib_time);
            tvIsya = itemView.findViewById(R.id.tv_isya_time);

            ivAlarmImsak = itemView.findViewById(R.id.iv_alarm_imsak);
            ivAlarmSubuh = itemView.findViewById(R.id.iv_alarm_shubuh);
            ivAlarmTerbit = itemView.findViewById(R.id.iv_alarm_terbit);
            ivAlarmDhuha = itemView.findViewById(R.id.iv_alarm_dhuha);
            ivAlarmDzuhur = itemView.findViewById(R.id.iv_alarm_dzuhur);
            ivAlarmAshar = itemView.findViewById(R.id.iv_alarm_ashar);
            ivAlarmMaghrib = itemView.findViewById(R.id.iv_alarm_maghrib);
            ivAlarmIsya = itemView.findViewById(R.id.iv_alarm_isya);
        }
    }
}