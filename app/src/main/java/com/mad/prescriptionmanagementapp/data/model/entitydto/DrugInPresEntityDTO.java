package com.mad.prescriptionmanagementapp.data.model.entitydto;


import androidx.room.Embedded;
import androidx.room.Relation;

import com.mad.prescriptionmanagementapp.data.model.entity.DrugEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DrugInPresEntityDTO {
    @Embedded
    private DrugInPresEntity drugInPresEntity;
    @Relation(parentColumn = "prescription_id", entityColumn = "local_id")
    private PrescriptionEntity prescriptionEntity;
    @Relation(parentColumn = "unit_id", entityColumn = "id")
    private UnitEntity unitEntity;
    @Relation(parentColumn = "drug_id", entityColumn = "id")
    private DrugEntity drugEntity;
}