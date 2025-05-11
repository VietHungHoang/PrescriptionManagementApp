package com.mad.prescriptionmanagementapp.data.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.room.Relation;

import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(tableName = "schedules",
        foreignKeys = {@ForeignKey(entity = DrugInPresEntity.class, parentColumns = "local_id", childColumns = "drug_in_pres_id", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = TimeDosageEntity.class, parentColumns = "local_id", childColumns = "time_dosage_id", onDelete = ForeignKey.CASCADE)},
        indices = {
                @Index(value = {"schedule_date_time_millis"})
        }
)
@NoArgsConstructor
@Data
public class ScheduleEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_id")
    private long localId;

    @ColumnInfo(name = "drug_in_pres_id")
    private Long drugInPresId;

    @ColumnInfo(name = "time_dosage_id")
    private Long timeDosageId;

    @ColumnInfo(name = "schedule_date_time_millis")
    private long scheduledDateTimeMillis;
    private ReminderStatus status;

    @ColumnInfo(name = "alarm_manager_request_id")
    private int alarmManagerRequestId;

    @ColumnInfo(name = "is_synced")
    private Boolean isSynced;

    public ScheduleEntity(Long drugInPresId, Long timeDosageId, long scheduledDateTimeMillis, ReminderStatus status, int alarmManagerRequestId) {
        this.drugInPresId = drugInPresId;
        this.timeDosageId = timeDosageId;
        this.scheduledDateTimeMillis = scheduledDateTimeMillis;
        this.status = status;
        this.alarmManagerRequestId = alarmManagerRequestId;
    }
}