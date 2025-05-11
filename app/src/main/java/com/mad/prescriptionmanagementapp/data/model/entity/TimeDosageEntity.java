package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(tableName = "time_dosages",
        foreignKeys = {@ForeignKey(entity = DrugInPresEntity.class,
                parentColumns = "local_id",
                childColumns = "drug_in_pres_id",
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "drug_in_pres_id")}
)
public class TimeDosageEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    public long localId;

    @ColumnInfo(name = "drug_in_pres_id")
    public long drugInPresId;

    public double dosage;
}