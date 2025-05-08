package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "schedules", // Đổi tên nếu có bảng cũ
        foreignKeys = {
                @ForeignKey(entity = DrugInPresEntity.class,
                        parentColumns = "localId", // Liên kết với localId của DrugInPresEntity
                        childColumns = "drugInPresLocalId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = DrugEntity.class,
                        parentColumns = "id",
                        childColumns = "drugId",
                        onDelete = ForeignKey.CASCADE) // Để dễ truy vấn thông tin thuốc
        },
        indices = {
                @Index(value = "drugInPresLocalId"),
                @Index(value = "drugId"),
                @Index(value = "reminderTime", unique = true) // Mỗi thời điểm nhắc nhở cho một thuốc là duy nhất
                // Cân nhắc nếu có thể có 2 liều cùng lúc (khác thuốc)
        }
)
public class ScheduleEntity {
    @PrimaryKey(autoGenerate = true)
    public long id; // Khóa chính tự tăng

    public long drugInPresLocalId; // FK - Biết được instance này thuộc lịch trình nào
    public long drugId;            // FK - Biết được đây là thuốc gì

    public long reminderTime;      // Thời gian chính xác cần nhắc (timestamp milliseconds UTC)
    public double dosageToTake;    // Liều lượng cần uống tại thời điểm này
    public String unitNameForDisplay; // Tên đơn vị (lấy từ UnitEntity, lưu vào đây để tiện hiển thị)

    public long actualTakenTime;   // Thời gian thực tế uống (timestamp, 0 nếu chưa uống)
    public String status;          // PENDING, TAKEN, SKIPPED, SNOOZED

    public int snoozeCount;

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_TAKEN = "TAKEN";
    public static final String STATUS_SKIPPED = "SKIPPED";
    public static final String STATUS_SNOOZED = "SNOOZED";

    public ScheduleEntity(long drugInPresLocalId, long drugId, long reminderTime,
                                  double dosageToTake, String unitNameForDisplay, String status) {
        this.drugInPresLocalId = drugInPresLocalId;
        this.drugId = drugId;
        this.reminderTime = reminderTime;
        this.dosageToTake = dosageToTake;
        this.unitNameForDisplay = unitNameForDisplay;
        this.status = status;
        this.snoozeCount = 0;
        this.actualTakenTime = 0;
    }
    public ScheduleEntity() {}
}