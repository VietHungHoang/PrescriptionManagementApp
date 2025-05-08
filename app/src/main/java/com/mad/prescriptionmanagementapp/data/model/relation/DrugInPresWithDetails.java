package com.mad.prescriptionmanagementapp.data.model.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mad.prescriptionmanagementapp.data.model.entity.DrugEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.TimeDosageEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.UnitEntity;

import java.util.List;

public class DrugInPresWithDetails {
    @Embedded
    public DrugInPresEntity drugInPres;

    @Relation(
            parentColumn = "drugId", // drugId từ DrugInPresEntity
            entityColumn = "id"      // id từ DrugEntity
    )
    public DrugEntity drugInfo;

    @Relation(
            parentColumn = "unitId", // unitId từ DrugInPresEntity
            entityColumn = "id"      // id từ UnitEntity
    )
    public UnitEntity unitInfo;

    @Relation(
            parentColumn = "localId",        // localId từ DrugInPresEntity
            entityColumn = "drugInPresLocalId" // drugInPresLocalId từ TimeDosageEntity
    )
    public List<TimeDosageEntity> timeDosages;
}