package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.model.entitydto.ScheduleEntityDTO;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ScheduleEntity reminder);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ScheduleEntity> reminders);

    @Update
    void update(ScheduleEntity schedule);

    @Query("SELECT * FROM schedules WHERE local_id = :id")
    ScheduleEntity getById(long id);

    @Query("SELECT * FROM schedules WHERE alarm_manager_request_id = :requestId")
    ScheduleEntity getByAlarmManagerRequestId(int requestId);

    @Query("SELECT * FROM schedules WHERE status = :status AND schedule_date_time_millis >= :currentTimeMillis ORDER BY schedule_date_time_millis ASC")
    List<ScheduleEntity> getPendingReminders(ReminderStatus status, long currentTimeMillis);

    // Lấy các reminder có cùng thời gian (ví dụ trong khoảng 1 phút)
    @Query("SELECT * FROM schedules WHERE schedule_date_time_millis BETWEEN :startTimeMillis AND :endTimeMillis AND status = :status")
    List<ScheduleEntity> getRemindersAroundTime(long startTimeMillis, long endTimeMillis, ReminderStatus status);


    @Query("UPDATE schedules SET status = :newStatus WHERE local_id = :id")
    void updateStatus(long id, ReminderStatus newStatus);

    @Query("UPDATE schedules SET status = :newStatus, schedule_date_time_millis = :newTime WHERE local_id = :id")
    void updateStatusAndSnoozeTime(long id, String newStatus, long newTime);


    @Query("DELETE FROM schedules WHERE local_id = :id")
    void deleteById(long id);

    // LiveData để quan sát thay đổi (nếu bạn dùng trong UI)
    @Query("SELECT * FROM schedules ORDER BY schedule_date_time_millis DESC")
    LiveData<List<ScheduleEntity>> getAllRemindersLiveData();

    @Transaction
    @Query("SELECT * FROM schedules")
    public List<ScheduleEntityDTO> getAllScheduleWithRelations();
}