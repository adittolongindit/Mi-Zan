package com.example.mi_zan.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;
import com.example.mi_zan.db.Alarm;
import java.util.Calendar;

public class PenjadwalanAlarm {

    public static void onAlarm(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Pastikan waktu alarm masih di masa depan
        if (alarm.getTriggerTimeMillis() < System.currentTimeMillis()) {
            return;
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("PRAYER_NAME", alarm.getPrayerName());

        int requestCode = alarm.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getTriggerTimeMillis(), pendingIntent);
                Toast.makeText(context, "Alarm " + alarm.getPrayerName() + " diaktifkan.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Izin untuk alarm presisi tidak diberikan.", Toast.LENGTH_SHORT).show();
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getTriggerTimeMillis(), pendingIntent);
            Toast.makeText(context, "Alarm " + alarm.getPrayerName() + " diaktifkan.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void offAlarm(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = alarm.getId().hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "Alarm " + alarm.getPrayerName() + " dinonaktifkan.", Toast.LENGTH_SHORT).show();
    }
}