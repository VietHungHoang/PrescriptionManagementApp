package com.mad.prescriptionmanagementapp.data.model.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mad.prescriptionmanagementapp.data.model.entity.DrugEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;

public class ScheduleWithDrug {
        @Embedded
        public ScheduleEntity scheduleEntity;

        @Relation(
                parentColumn = "drugId", // drugId từ ReminderInstanceEntity
                entityColumn = "id"      // id từ DrugEntity
        )
        public DrugEntity drugInfo;
}
