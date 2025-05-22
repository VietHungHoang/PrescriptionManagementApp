package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.mad.prescriptionmanagementapp.data.database.Converters;
import com.mad.prescriptionmanagementapp.util.Tools;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(tableName = "prescriptions")
@TypeConverters(Converters.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionEntity {
    @PrimaryKey
    @ColumnInfo(name = "local_id")
    private Long localId;

    private String name;

    private String hospital;

    @ColumnInfo(name = "doctor_name")
    private String doctorName;

    @ColumnInfo(name = "consultation_date")
    private LocalDate consultationDate;

    @ColumnInfo(name = "follow_up_date")
    private LocalDate followUpDate;

    @ColumnInfo(name = "is_synced")
    private Boolean isSynced;

    @ColumnInfo(name = "server_id")
    private Long serverId;
}