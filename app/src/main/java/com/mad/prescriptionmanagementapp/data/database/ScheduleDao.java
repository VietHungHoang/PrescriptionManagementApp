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

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ScheduleEntity reminder);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ScheduleEntity> reminders);

//    @Update
//    void update(ScheduleEntityDTO schedule);

    @Query("SELECT * FROM schedules WHERE local_id = :id")
    ScheduleEntityDTO getById(long id);

    @Query("SELECT * FROM schedules WHERE alarm_manager_request_id = :requestId")
    List<ScheduleEntityDTO> getByAlarmManagerRequestId(int requestId);

    @Query("SELECT * FROM schedules WHERE alarm_manager_request_id = :requestId AND (status = :status1 OR status = :status2 )")
    List<ScheduleEntityDTO> getSnozeeByRequestIdAndStatus(int requestId, ReminderStatus status1, ReminderStatus status2);

    @Query("SELECT * FROM schedules WHERE status = :status AND date_time >= :currentDateTime ORDER BY date_time ASC")
    List<ScheduleEntityDTO> getPendingReminders(ReminderStatus status, LocalDateTime currentDateTime);

    @Query("SELECT * FROM schedules WHERE status = :status AND alarm_manager_request_id = :alarmRequestId")
    List<ScheduleEntityDTO> getByRequestId(ReminderStatus status, int alarmRequestId);

    @Query("SELECT * FROM schedules s\n" +
            "JOIN drug_in_prescriptions dip ON s.drug_in_pres_id = dip.local_id\n" +
            "WHERE s.status = :status\n" +
            "  AND s.date_time >= :currentDateTime\n" +
            "  AND s.date_time IN (\n" +
            "    SELECT s2.date_time\n" +
            "    FROM schedules s2\n" +
            "    JOIN drug_in_prescriptions dip2 ON s2.drug_in_pres_id = dip2.local_id\n" +
            "    WHERE dip2.prescription_id = (SELECT MAX(prescription_id) FROM drug_in_prescriptions)\n" +
            "  )\n" +
            "ORDER BY s.date_time ASC\n")
    List<ScheduleEntityDTO> getCurrentReminders(ReminderStatus status, LocalDateTime currentDateTime);

    @Query("SELECT * FROM schedules s\n" +
            "JOIN drug_in_prescriptions dip ON s.drug_in_pres_id = dip.local_id\n" +
            "WHERE s.status = :status\n" +
            "  AND s.date_time >= :currentDateTime\n" +
            "  AND s.date_time IN (\n" +
            "    SELECT s2.date_time\n" +
            "    FROM schedules s2\n" +
            "    JOIN drug_in_prescriptions dip2 ON s2.drug_in_pres_id = dip2.local_id\n" +
            "    WHERE dip2.prescription_id = (SELECT MAX(prescription_id) FROM drug_in_prescriptions)\n" +
            "  )\n" +
            "ORDER BY s.date_time ASC\n")
    List<ScheduleEntity> getCurrentHandler(ReminderStatus status, LocalDateTime currentDateTime);

    // Lấy các reminder có cùng thời gian (ví dụ trong khoảng 1 phút)
//    @Query("SELECT * FROM schedules WHERE schedule_date_time_millis BETWEEN :startTimeMillis AND :endTimeMillis AND status = :status")
//    List<ScheduleEntityDTO> getRemindersAroundTime(long startTimeMillis, long endTimeMillis, ReminderStatus status);


    @Query("UPDATE schedules SET status = :newStatus WHERE local_id = :id")
    void updateStatus(long id, ReminderStatus newStatus);

    @Query("UPDATE schedules SET status = :newStatus, date_time = :newTime WHERE local_id = :id")
    void updateStatusAndSnoozeTime(long id, String newStatus, LocalDateTime newTime);

    @Update
    void updateSchedules(List<ScheduleEntity> schedules);

    @Query("DELETE FROM schedules WHERE local_id = :id")
    void deleteById(long id);

    // LiveData để quan sát thay đổi (nếu bạn dùng trong UI)
//    @Query("SELECT * FROM schedules ORDER BY schedule_date_time_millis DESC")
//    LiveData<List<ScheduleEntity>> getAllRemindersLiveData();

    @Transaction
    @Query("SELECT * FROM schedules")
    public List<ScheduleEntityDTO> getAllScheduleWithRelations();
}