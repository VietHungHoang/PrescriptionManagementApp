package com.mad.prescriptionmanagementapp.data.model.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;
import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;

import java.util.List;

public class PrescriptionWithDrugDetails {
    @Embedded
    public PrescriptionEntity prescription;

    @Relation(
            entity = DrugInPresEntity.class, // Phải chỉ định Entity rõ ràng
            parentColumn = "id", // id của PrescriptionEntity
            entityColumn = "prescriptionId" // prescriptionId của DrugInPresEntity
    )
    public List<DrugInPresWithDetails> drugDetailsList; // Danh sách các thuốc trong đơn với chi tiết
}