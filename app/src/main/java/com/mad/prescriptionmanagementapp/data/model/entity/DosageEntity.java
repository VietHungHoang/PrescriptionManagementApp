package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(tableName = "dosages")

//@Entity(tableName = "time_dosages",
//        foreignKeys = {@ForeignKey(entity = DrugInPresEntity.class,
//                parentColumns = "local_id",
//                childColumns = "drug_in_pres_id",
//                onDelete = ForeignKey.CASCADE)},
//        indices = {@Index(value = "drug_in_pres_id")}
//)
public class DosageEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    private Long localId;

//    @ColumnInfo(name = "drug_in_pres_id")
//    private long drugInPresId;

    private double dosage;

    @ColumnInfo(name = "server_id")
    private Long serverId;

}