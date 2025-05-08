package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "time_dosages",
        foreignKeys = @ForeignKey(entity = DrugInPresEntity.class,
                parentColumns = "localId", // Liên kết với localId của DrugInPresEntity
                childColumns = "drugInPresLocalId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "drugInPresLocalId")}
)
public class TimeDosageEntity {
    @PrimaryKey(autoGenerate = true)
    public long localId; // Khóa chính cục bộ

    public long drugInPresLocalId; // FK

    public int hour;
    public int minutes;
    public double dosage;

    public TimeDosageEntity(long drugInPresLocalId, int hour, int minutes, double dosage) {
        this.drugInPresLocalId = drugInPresLocalId;
        this.hour = hour;
        this.minutes = minutes;
        this.dosage = dosage;
    }
    public TimeDosageEntity() {}
}