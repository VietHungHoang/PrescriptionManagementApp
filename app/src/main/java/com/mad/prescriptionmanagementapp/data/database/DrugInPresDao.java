package com.mad.prescriptionmanagementapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mad.prescriptionmanagementapp.data.model.entity.DrugInPresEntity;

import java.util.List;

@Dao
public interface DrugInPresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Nếu API ID giống nhau thì replace
    long insert(DrugInPresEntity drugInPres);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DrugInPresEntity> items);

    @Query("SELECT * FROM drug_in_prescriptions WHERE prescription_id = :prescriptionId")
    LiveData<List<DrugInPresEntity>> getDrugsInPrescriptionRaw(Long prescriptionId);

    // Lấy một DrugInPres và các TimeDosage của nó
//    @Transaction
//    @Query("SELECT * FROM drug_in_prescriptions WHERE localId = :localId")
//    LiveData<DrugInPresWithTimeDosages> getDrugInPresWithTimeDosages(long localId);
//
//    // Lấy tất cả DrugInPres (cùng TimeDosages) cho một prescriptionId
//    @Transaction
//    @Query("SELECT * FROM drug_in_prescriptions WHERE prescriptionId = :prescriptionId")
//    LiveData<List<DrugInPresWithTimeDosages>> getAllDrugInPresWithTimeDosagesForPrescription(Long prescriptionId);

    // Lấy tất cả DrugInPresEntity (không join) để tạo ReminderInstance
//    @Query("SELECT * FROM drug_in_prescriptions")
//    List<DrugInPresEntity> getAllDrugInPresSync();
//
//    @Query("SELECT * FROM drug_in_prescriptions WHERE local_id = :localId")
//    DrugInPresEntity getDrugInPresByIdSync(long localId);
//
//    @Query("DELETE FROM drug_in_prescriptions WHERE prescription_id = :prescriptionId")
//    void deleteByPrescriptionId(Long prescriptionId);
}