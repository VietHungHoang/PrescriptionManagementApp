package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "scheduled_reminders",
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
public class ScheduledReminderEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public Long prescriptionId; // ID của đơn thuốc gốc
    public Long drugInPresId;   // ID của thuốc trong đơn thuốc (để liên kết với DrugInPres)

    public String drugName;
    public double dosage;
    public String unitName;
    public String drugImage; // Thêm ảnh thuốc nếu có

    public long scheduledDateTimeMillis; // Thời gian UTC để nhắc nhở
    public String status; // Ví dụ: "PENDING", "NOTIFIED", "CONFIRMED", "SKIPPED", "SNOOZED"

    // ID dùng cho AlarmManager để có thể hủy.
    // Có thể dùng chính `id` ở trên làm request code, nhưng để riêng cho rõ ràng
    public int alarmManagerRequestId;

    public ScheduledReminderEntity(Long prescriptionId, Long drugInPresId, String drugName,
                                   double dosage, String unitName, String drugImage,
                                   long scheduledDateTimeMillis, String status, int alarmManagerRequestId) {
        this.prescriptionId = prescriptionId;
        this.drugInPresId = drugInPresId;
        this.drugName = drugName;
        this.dosage = dosage;
        this.unitName = unitName;
        this.drugImage = drugImage; // Khởi tạo
        this.scheduledDateTimeMillis = scheduledDateTimeMillis;
        this.status = status;
        this.alarmManagerRequestId = alarmManagerRequestId;
    }

    // Getters and setters (hoặc để public fields nếu bạn muốn)
    // ...
}