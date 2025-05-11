package com.mad.prescriptionmanagementapp.data.model;
import androidx.room.Relation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Schedule {
    private long id;

    @JsonProperty("drug_in_pres_id")
    private Long drugInPresId;   // ID của thuốc trong đơn thuốc (để liên kết với DrugInPres)

    @JsonProperty("time_dosage_id")
    private Long timeDosageId;

    @JsonProperty("schedule_date_time_millis")
    private long scheduledDateTimeMillis; // Thời gian UTC để nhắc nhở
    private ReminderStatus status; // Ví dụ: "PENDING", "NOTIFIED", "CONFIRMED", "SKIPPED", "SNOOZED"


}