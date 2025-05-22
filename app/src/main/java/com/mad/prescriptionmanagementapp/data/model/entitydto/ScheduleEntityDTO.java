package com.mad.prescriptionmanagementapp.data.model.entitydto;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.ScheduleEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.DosageEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScheduleEntityDTO {
    @Embedded
    ScheduleEntity scheduleEntity;

    @Relation(parentColumn = "drug_in_pres_id", entityColumn = "local_id", entity = DrugInPresEntity.class)
    public DrugInPresEntityDTO drugInPresDTO;

    @Relation(parentColumn = "dosage_id", entityColumn = "local_id")
    public DosageEntity dosage;
}