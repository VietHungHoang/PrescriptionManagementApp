package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.model.relation.ScheduleWithDrug;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Nếu reminderTime + drugId đã tồn tại, thay thế (hữu ích khi snooze)
    long insert(ScheduleEntity scheduleEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE) // IGNORE khi tạo loạt, nếu có trùng thì bỏ qua
    void insertAll(List<ScheduleEntity> scheduleEntity);

    @Update
    int update(ScheduleEntity scheduleEntity); // Trả về số hàng bị ảnh hưởng

    @Query("SELECT * FROM schedules WHERE id = :id")
    ScheduleEntity getScheduleByIdSync(long id);

    @Transaction // Để join với DrugEntity
    @Query("SELECT * FROM schedules WHERE id = :id")
    LiveData<ScheduleWithDrug> getReminderInstanceWithDrugById(long id);

    @Query("SELECT * FROM schedules WHERE (status = :statusPending OR status = :statusSnoozed) AND reminderTime <= :currentTimeMillis ORDER BY reminderTime ASC")
    List<ScheduleEntity> getPendingOrSnoozedRemindersBefore(long currentTimeMillis, String statusPending, String statusSnoozed);

    @Transaction
    @Query("SELECT * FROM schedules WHERE (status = :statusPending OR status = :statusSnoozed) AND reminderTime >= :fromTimeMillis ORDER BY reminderTime ASC")
    LiveData<List<ScheduleWithDrug>> getUpcomingRemindersWithDrug(long fromTimeMillis, String statusPending, String statusSnoozed);

    // Xóa các reminder trong tương lai cho một lịch trình thuốc cụ thể (khi lịch trình thay đổi/bị xóa)
    @Query("DELETE FROM schedules WHERE drugInPresLocalId = :drugInPresLocalId AND reminderTime > :currentTimeMillis AND (status = :statusPending OR status = :statusSnoozed)")
    void deleteFuturePendingSchedulesForDrugInPres(long drugInPresLocalId, long currentTimeMillis, String statusPending, String statusSnoozed);

    @Query("SELECT * FROM schedules WHERE status = :statusPending OR status = :statusSnoozed")
    List<ScheduleEntity> getAllPendingOrSnoozedSchedulesSync(String statusPending, String statusSnoozed);

    @Query("DELETE FROM schedules WHERE drugInPresLocalId = :drugInPresLocalId")
    void deleteAllSchedulesForDrugInPres(long drugInPresLocalId);

    @Query("DELETE FROM schedules WHERE reminderTime < :olderThanTimestamp AND (status = :statusTaken OR status = :statusSkipped)")
    void deleteOldTakenOrSkippedSchedules(long olderThanTimestamp, String statusTaken, String statusSkipped);
}