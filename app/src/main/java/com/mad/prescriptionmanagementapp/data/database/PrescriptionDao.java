package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mad.prescriptionmanagementapp.data.model.entity.PrescriptionEntity;
import com.mad.prescriptionmanagementapp.data.model.relation.PrescriptionWithDrugDetails;

import java.util.List;

@Dao
public interface PrescriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PrescriptionEntity prescription);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PrescriptionEntity> prescriptions);

    @Query("SELECT * FROM prescriptions WHERE id = :id")
    LiveData<PrescriptionEntity> getPrescriptionById(Long id);

    @Query("SELECT * FROM prescriptions ORDER BY consultationDate DESC")
    LiveData<List<PrescriptionEntity>> getAllPrescriptions();

    // Lấy đơn thuốc và tất cả các chi tiết thuốc trong đơn đó
    @Transaction
    @Query("SELECT * FROM prescriptions WHERE id = :prescriptionId")
    LiveData<PrescriptionWithDrugDetails> getPrescriptionWithDetailsById(Long prescriptionId);

    @Transaction
    @Query("SELECT * FROM prescriptions ORDER BY consultationDate DESC")
    LiveData<List<PrescriptionWithDrugDetails>> getAllPrescriptionsWithDetails();

    @Query("DELETE FROM prescriptions WHERE id = :prescriptionId")
    void deletePrescriptionById(long prescriptionId); // Xóa đơn thuốc (sẽ cascade xóa DrugInPres, TimeDosage, ReminderInstance)
}