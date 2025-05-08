package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "schedules",
        foreignKeys = {
                // Giả sử bạn có PrescriptionEntity và DrugInPresEntity
                // @ForeignKey(entity = PrescriptionEntity.class, parentColumns = "id", childColumns = "prescriptionId", onDelete = ForeignKey.CASCADE),
                // @ForeignKey(entity = DrugInPresEntity.class, parentColumns = "id", childColumns = "drugInPresId", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                // @Index(value = {"prescriptionId"}),
                // @Index(value = {"drugInPresId"}),
                @Index(value = {"scheduledDateTimeMillis"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ScheduleEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "prescription_id")
    private Long prescriptionId; // ID của đơn thuốc gốc
    @ColumnInfo(name = "drug_in_pres_id")
    private Long drugInPresId;   // ID của thuốc trong đơn thuốc (để liên kết với DrugInPres)

    @ColumnInfo(name="drug_name")
    private String drugName;
    private double dosage;
    @ColumnInfo(name="unit_name")
    private String unitName;

    @ColumnInfo(name="schedule_date_time_millis")
    private long scheduledDateTimeMillis; // Thời gian UTC để nhắc nhở
    private ReminderStatus status; // Ví dụ: "PENDING", "NOTIFIED", "CONFIRMED", "SKIPPED", "SNOOZED"

    // ID dùng cho AlarmManager để có thể hủy.
    // Có thể dùng chính `id` ở trên làm request code, nhưng để riêng cho rõ ràng
    @ColumnInfo(name="alarm_manager_request_id")
    private int alarmManagerRequestId;

}