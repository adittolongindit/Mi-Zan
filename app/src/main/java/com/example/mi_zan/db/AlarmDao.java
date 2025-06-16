package com.example.mi_zan.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Alarm alarm);

    @Query("SELECT * FROM alarms WHERE id = :id")
    Alarm getAlarmById(String id);
}