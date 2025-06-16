package com.example.mi_zan.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_zan.R;
import com.example.mi_zan.db.Alarm;
import com.example.mi_zan.db.AppDatabase;
import com.example.mi_zan.model.JadwalItem;
import com.example.mi_zan.model.WaktuSholatItem;
import com.example.mi_zan.service.PenjadwalanAlarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class JadwalHarianAdapter extends RecyclerView.Adapter<JadwalHarianAdapter.ViewHolder> {

    private final List<WaktuSholatItem> jadwalList;
    private final JadwalItem dailySchedule;
    private final Context context;

    public JadwalHarianAdapter(List<WaktuSholatItem> jadwalList, JadwalItem dailySchedule, Context context) {
        this.jadwalList = jadwalList;
        this.dailySchedule = dailySchedule;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jadwal_harian, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaktuSholatItem item = jadwalList.get(position);
        holder.tvPrayerName.setText(item.getName());
        holder.tvPrayerTime.setText(item.getTime());

        setupAlarmIcon(holder.ivAlarmToggle, item.getName(), item.getTime());
    }

    private void setupAlarmIcon(ImageView alarmIcon, String prayerName, String prayerTime) {
        String alarmId = dailySchedule.getDate() + "-" + prayerName;

        AsyncTask.execute(() -> {
            Alarm existingAlarm = AppDatabase.getDatabase(context).alarmDao().getAlarmById(alarmId);
            final boolean isCurrentlyEnabled = existingAlarm != null && existingAlarm.isEnabled();

            alarmIcon.post(() -> {
                updateAlarmIconUI(alarmIcon, isCurrentlyEnabled);
                alarmIcon.setOnClickListener(v -> toggleAlarm(alarmIcon, prayerName, prayerTime, alarmId));
            });
        });
    }

    private void toggleAlarm(ImageView alarmIcon, String prayerName, String prayerTime, String alarmId) {
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
        return jadwalList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrayerName, tvPrayerTime;
        ImageView ivAlarmToggle;

        ViewHolder(View itemView) {
            super(itemView);
            tvPrayerName = itemView.findViewById(R.id.tv_prayer_name);
            tvPrayerTime = itemView.findViewById(R.id.tv_prayer_time);
            ivAlarmToggle = itemView.findViewById(R.id.iv_alarm_toggle);
        }
    }
}