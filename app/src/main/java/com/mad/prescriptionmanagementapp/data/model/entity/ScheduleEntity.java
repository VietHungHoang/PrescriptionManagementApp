package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.mad.prescriptionmanagementapp.data.database.Converters;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "schedules",
        foreignKeys = {@ForeignKey(entity = DrugInPresEntity.class, parentColumns = "local_id", childColumns = "drug_in_pres_id", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = DosageEntity.class, parentColumns = "local_id", childColumns = "dosage_id", onDelete = ForeignKey.CASCADE)},
        indices = {
                @Index(value = {"date_time"})
        }
)
@TypeConverters(Converters.class)
@NoArgsConstructor
@Data
public class ScheduleEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    private long localId;

    @ColumnInfo(name = "drug_in_pres_id")
    private Long drugInPresId;

    @ColumnInfo(name = "dosage_id")
    private Long dosageId;

    @ColumnInfo(name = "date_time")
    private LocalDateTime dateTime;
    private ReminderStatus status;

    @ColumnInfo(name = "alarm_manager_request_id")
    private int alarmManagerRequestId;

    @ColumnInfo(name = "server_id")
    private Long serverId;

    @ColumnInfo(name = "is_synced")
    private Boolean isSynced;

    public ScheduleEntity(Long drugInPresId, Long dosageId, LocalDateTime dateTime, ReminderStatus status, int alarmManagerRequestId) {
        this.drugInPresId = drugInPresId;
        this.dosageId = dosageId;
        this.dateTime = dateTime;
        this.status = status;
        this.alarmManagerRequestId = alarmManagerRequestId;
    }
}