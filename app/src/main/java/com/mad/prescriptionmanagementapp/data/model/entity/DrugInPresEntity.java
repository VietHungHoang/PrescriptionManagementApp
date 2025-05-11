package com.mad.prescriptionmanagementapp.data.model.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import com.mad.prescriptionmanagementapp.data.database.Converters;
import com.mad.prescriptionmanagementapp.data.model.DrugInPres;
import com.mad.prescriptionmanagementapp.data.model.entitydto.DrugInPresEntityDTO;
//import com.mad.prescriptionmanagementapp.data.model.entitydto.PrescriptionEntityDTO;
import com.mad.prescriptionmanagementapp.util.Frequency;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "drug_in_prescriptions",
        foreignKeys = {
                @ForeignKey(entity = PrescriptionEntity.class, parentColumns = "local_id", childColumns = "prescription_id", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = DrugEntity.class, parentColumns = "id", childColumns = "drug_id", onDelete = ForeignKey.CASCADE), // Không cho xóa thuốc nếu đang dùng
                @ForeignKey(entity = UnitEntity.class, parentColumns = "id", childColumns = "unit_id", onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = "prescription_id"),
                @Index(value = "drug_id"),
                @Index(value = "unit_id")
        }
)
@TypeConverters(Converters.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugInPresEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    private Long localId;

    @ColumnInfo(name = "prescription_id")
    private Long prescriptionId;

    @ColumnInfo(name = "drug_id")
    private Long drugId;

    @ColumnInfo(name = "unit_id")
    private Long unitId;

    @ColumnInfo(name = "start_date")
    private LocalDate startDate;

    private Frequency frequency;

    @ColumnInfo(name = "every_n_days")
    private int everyNDays;

    @ColumnInfo(name = "specific_days")
    private List<Integer> specificDays;

    private String note;

    @ColumnInfo(name = "is_synced")
    private Boolean isSynced;

    public DrugInPresEntity(Long prescriptionId, Long drugId, Long unitId, LocalDate startDate, Frequency frequency, int everyNDays, List<Integer> specificDays) {
        this.prescriptionId = prescriptionId;
        this.drugId = drugId;
        this.unitId = unitId;
        this.startDate = startDate;
        this.frequency = frequency;
        this.everyNDays = everyNDays;
        this.specificDays = specificDays;
    }

    public static DrugInPresEntity modelToEntity(DrugInPres drugInPres) {
        return new DrugInPresEntity(null, drugInPres.getDrug().getId(),1L, null, drugInPres.getFrequency(), drugInPres.getEveryNDays(), drugInPres.getSpecificDays());
    }
}