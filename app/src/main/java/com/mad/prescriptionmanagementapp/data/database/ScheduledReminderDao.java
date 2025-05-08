package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mad.prescriptionmanagementapp.data.model.entity.ScheduledReminderEntity;

import java.util.List;

@Dao
public interface ScheduledReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ScheduledReminderEntity reminder);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ScheduledReminderEntity> reminders);

    @Update
    void update(ScheduledReminderEntity reminder);

    @Query("SELECT * FROM scheduled_reminders WHERE id = :id")
    ScheduledReminderEntity getById(long id);

    @Query("SELECT * FROM scheduled_reminders WHERE alarmManagerRequestId = :requestId")
    ScheduledReminderEntity getByAlarmManagerRequestId(int requestId);

    @Query("SELECT * FROM scheduled_reminders WHERE status = :status AND scheduledDateTimeMillis >= :currentTimeMillis ORDER BY scheduledDateTimeMillis ASC")
    List<ScheduledReminderEntity> getPendingReminders(String status, long currentTimeMillis);

    // Lấy các reminder có cùng thời gian (ví dụ trong khoảng 1 phút)
    @Query("SELECT * FROM scheduled_reminders WHERE scheduledDateTimeMillis BETWEEN :startTimeMillis AND :endTimeMillis AND status = :status")
    List<ScheduledReminderEntity> getRemindersAroundTime(long startTimeMillis, long endTimeMillis, String status);


    @Query("UPDATE scheduled_reminders SET status = :newStatus WHERE id = :id")
    void updateStatus(long id, String newStatus);

    @Query("UPDATE scheduled_reminders SET status = :newStatus, scheduledDateTimeMillis = :newTime WHERE id = :id")
    void updateStatusAndSnoozeTime(long id, String newStatus, long newTime);


    @Query("DELETE FROM scheduled_reminders WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM scheduled_reminders WHERE prescriptionId = :prescriptionId")
    void deleteByPrescriptionId(long prescriptionId);

    // LiveData để quan sát thay đổi (nếu bạn dùng trong UI)
    @Query("SELECT * FROM scheduled_reminders ORDER BY scheduledDateTimeMillis DESC")
    LiveData<List<ScheduledReminderEntity>> getAllRemindersLiveData();
}