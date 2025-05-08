package com.mad.prescriptionmanagementapp.data.model.entity;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.mad.prescriptionmanagementapp.data.database.Converters;

import java.time.LocalDate;
import java.util.List;

@Entity(tableName = "drug_in_prescriptions",
        foreignKeys = {
                @ForeignKey(entity = PrescriptionEntity.class, parentColumns = "id", childColumns = "prescriptionId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = DrugEntity.class, parentColumns = "id", childColumns = "drugId", onDelete = ForeignKey.RESTRICT), // Không cho xóa thuốc nếu đang dùng
                @ForeignKey(entity = UnitEntity.class, parentColumns = "id", childColumns = "unitId", onDelete = ForeignKey.RESTRICT)
        },
        indices = {
                @Index(value = "prescriptionId"),
                @Index(value = "drugId"),
                @Index(value = "unitId")
        }
)
@TypeConverters(Converters.class)
public class DrugInPresEntity {
    @PrimaryKey(autoGenerate = true)
    public long localId; // Khóa chính cục bộ tự tăng cho bảng này

    public Long prescriptionId; // FK
    public Long drugId;         // FK (từ drugResponse.id)
    public Long unitId;         // FK (từ unit.id)

    // 'date' field từ model của bạn, giả sử là ngày bắt đầu uống thuốc này
    // Nếu 'date' có ý nghĩa khác, cần điều chỉnh kiểu và tên
    public LocalDate startDate;

    public Converters.Frequency frequency;
    public int everyNDays; // Chỉ có ý nghĩa nếu frequency = EVERY_N_DAYS
    public List<Integer> specificDays; // Chỉ có ý nghĩa nếu frequency = SPECIFIC_DATES
    // (ví dụ: [1, 15] cho ngày 1 và 15 hàng tháng,
    // hoặc các hằng số Calendar.MONDAY, Calendar.TUESDAY...)

    // List<TimeDosage> sẽ được biểu diễn qua TimeDosageEntity có drugInPresLocalId
    // Thêm trường này để map từ DTO của bạn nếu có
    public Long originalApiId; // Nếu DrugInPres có ID riêng từ API

    public DrugInPresEntity(Long prescriptionId, Long drugId, Long unitId, LocalDate startDate,
                            Converters.Frequency frequency, int everyNDays, List<Integer> specificDays, Long originalApiId) {
        this.prescriptionId = prescriptionId;
        this.drugId = drugId;
        this.unitId = unitId;
        this.startDate = startDate;
        this.frequency = frequency;
        this.everyNDays = everyNDays;
        this.specificDays = specificDays;
        this.originalApiId = originalApiId;
    }
    public DrugInPresEntity() {}
}